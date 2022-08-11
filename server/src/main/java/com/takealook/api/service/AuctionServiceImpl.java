package com.takealook.api.service;

import com.takealook.api.request.AuctionRegisterPostReq;
import com.takealook.api.request.AuctionUpdatePatchReq;
import com.takealook.api.request.ProductRegisterPostReq;
import com.takealook.common.util.HashUtil;
import com.takealook.db.entity.Auction;
import com.takealook.db.entity.AuctionImage;
import com.takealook.db.entity.Product;
import com.takealook.db.repository.AuctionImageRepository;
import com.takealook.db.repository.AuctionRepository;
import com.takealook.db.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuctionServiceImpl implements AuctionService {

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuctionImageRepository auctionImageRepository;

    @Autowired
    S3FileService s3FileService;

    @Override
    public Auction createAuction(Long memberSeq, AuctionRegisterPostReq auctionRegisterPostReq, List<MultipartFile> multipartFileList) {
        // 화상회의 링크 생성해야 함!!!!!!!!!!!!!!!!!!!!!!!!!!

        // title, content, categorySeq, startTime, productlist(productname, startprice), auctionImagelist(imageurl)
        String hash = HashUtil.MD5(LocalDateTime.now().toString() + memberSeq);
        Auction auction = Auction.builder()
                .hash(hash)
                .memberSeq(memberSeq)
                .title(auctionRegisterPostReq.getTitle())
                .content(auctionRegisterPostReq.getContent())
                .categorySeq(auctionRegisterPostReq.getCategorySeq())
                .startTime(auctionRegisterPostReq.getStartTime())
                .build();
        auctionRepository.save(auction);

        // 방금 저장한 경매 seq 뽑아서 product, auctionimage에 넣어야 함
        auction = auctionRepository.findFirstByMemberSeqOrderBySeqDesc(memberSeq);

        for (ProductRegisterPostReq productRegisterPostReq : auctionRegisterPostReq.getProductList()) {
            Product product = Product.builder()
                    .auctionSeq(auction.getSeq())
                    .productName(productRegisterPostReq.getProductName())
                    .startPrice(productRegisterPostReq.getStartPrice())
                    .finalPrice(productRegisterPostReq.getStartPrice())
                    .status(0)
                    .build();
            productRepository.save(product);
        }

        // 옥션 이미지 없을 시 기본이미지 넣기
        if (multipartFileList == null || multipartFileList.size() == 0) {
            auctionImageRepository.save(AuctionImage.builder()
                    .auctionSeq(auction.getSeq())
                    .imageUrl("/product/basic1.png")
                    .build());
        } else {
            // S3 버킷에 물품 이미지 저장 후 db에 imageUrl 저장
            try {
                List<String> imageUrlList = s3FileService.uploadProductIamge(multipartFileList, auction.getHash());
                for (String imageUrl : imageUrlList) {
                    AuctionImage auctionImage = AuctionImage.builder()
                            .auctionSeq(auction.getSeq())
                            .imageUrl(imageUrl)
                            .build();
                    auctionImageRepository.save(auctionImage);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return auction;
    }

    @Override
    public Auction getAuctionByHash(String hash) {
        Auction auction = auctionRepository.findByHash(hash).get();
        return auction;
    }

    @Override
    public Auction getAuctionBySeq(Long auctionSeq) {
        Auction auction = auctionRepository.findBySeq(auctionSeq).get();
        return auction;
    }

    // 실시간 경매 조회
    @Override
    public List<Auction> getLiveAuctionList() {
        List<Auction> auctionList = auctionRepository.findAllByStatus(1);
        return auctionList;
    }

    // 메인 - 경매임박순 정렬 검색 (24시간 이내만 조회)
    @Override
    public List<Auction> getAuctionListToday(Pageable pageable) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String tomorrowTime = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Auction> auctionList = auctionRepository.findAllByStartTimeAfterAndStartTimeBeforeOrderByStartTimeAsc(currentTime, tomorrowTime, pageable);
        return auctionList;
    }

    // 인기순, 최신순 정렬 검색 - startTime 현재시간보다 이후거나 현재 진행 중인 것들
    @Override
    public List<Auction> getAuctionList(String word, Pageable pageable) {
        List<Auction> auctionList = null;
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (word == null) {
            word = "";
        }
        auctionList = auctionRepository.findAllByTitleContainsOrContentContainsAndStartTimeAfterOrStatus(word, word, currentTime, 1, pageable);
        return auctionList;
    }

    // 판매자 신뢰도순 정렬 검색 - startTime 현재시간보다 이후거나 현재 진행 중인 것들
    @Override
    public List<Auction> getAuctionListOrderByScore(String word, Pageable pageable) {
        List<Auction> auctionList = null;
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (word == null) {
            word = "";
        }
        auctionList = auctionRepository.findAllByTitleOrContentContainsAndStartTimeAfterOrderByMemberScore(word, currentTime, 1, pageable);
        return auctionList;
    }

    @Override
    public List<Auction> getAuctionListByCategorySeqOrderByScore(Long categorySeq, Pageable pageable) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Auction> auctionList = null;
        if (categorySeq == 0) { // 전체 조회
            auctionList = auctionRepository.findAllByStartTimeAfterOrderByMemberScore(currentTime, 1, pageable);
        } else {
            auctionList = auctionRepository.findAllByCategorySeqAndStartTimeAfterOrderByMemberScore(categorySeq, currentTime, 1, pageable);
        }
        return auctionList;
    }

    @Override
    public List<Auction> getAuctionListByCategorySeq(Long categorySeq, Pageable pageable) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Auction> auctionList = null;
        if (categorySeq == 0) { // 전체 조회
            auctionList = auctionRepository.findAllByStatusOrStartTimeAfter(1, currentTime, pageable);
        } else {
            auctionList = auctionRepository.findAllByCategorySeqAndStartTimeAfterOrStatus(categorySeq, currentTime, 1, pageable);
        }
        return auctionList;
    }

    @Override
    public void updateAuction(Long memberSeq, AuctionUpdatePatchReq auctionUpdatePatchReq) {
        Auction auction = Auction.builder()
                .seq(auctionUpdatePatchReq.getSeq())
                .hash(auctionUpdatePatchReq.getHash())
                .memberSeq(memberSeq)
                .title(auctionUpdatePatchReq.getTitle())
                .content(auctionUpdatePatchReq.getContent())
                .categorySeq(auctionUpdatePatchReq.getCategorySeq())
                .startTime(auctionUpdatePatchReq.getStartTime())
                .status(auctionUpdatePatchReq.getStatus())
                .interest(auctionUpdatePatchReq.getInterest())
                .build();
        auctionRepository.save(auction);
    }

    @Override
    public void deleteAuction(Long memberSeq, Long auctionSeq) {
        Auction auction = auctionRepository.findBySeq(auctionSeq).orElse(null);
        if (auction != null && auction.getMemberSeq() == memberSeq) {
            auctionRepository.delete(auction);
        } else {
            // 예외처리
        }
    }
}