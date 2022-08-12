import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';

function IsSellingCard({ ModalHandler, sellingItem, auctionitem }) {
  // TODO: 데이터 교체하기

  const [statusName, setStatusName] = useState(0);
  useEffect(() => {
    setStatusName(sellingItem.status);
  }, []);
  console.log();

  const auctionstr = ['판매중', '입금대기', '입금완료', '판매완료', '유찰', '거래취소'];
  // API 연결후 acutionitem 전부 sellingItem으로 변경
  return (
    <CardContainer>
      <div>
        <Link to="/">
          <div>
            <picture>
              <img
                // src={sellingItem.auctionImage.imageUrl}
                alt="옥션이미지"
                width="400"
                height="300"
              />
            </picture>
            <AuctionInfoContainer>
              <div>
                <h4>{sellingItem.productName}</h4>
                {4 > statusName > 0 && <p>{sellingItem.finalPrice}</p>}
                {statusName >= 4 && <p>{sellingItem.startPrice}</p>}
                {statusName === 0 && <p>{sellingItem.startPrice}</p>}
                <AcutionTime>{sellingItem.startTime}</AcutionTime>
                <AuctionStatus auctionstrkey={sellingItem.status}>
                  {auctionstr[sellingItem.status]}
                </AuctionStatus>
              </div>
            </AuctionInfoContainer>
          </div>
        </Link>
        <Link to="/">
          <div>
            <picture>
              <img src={sellingItem.profileUrl} alt="유저 프로필" width="50" height="50" />
            </picture>
          </div>
        </Link>
      </div>
      <Plus type="button" onClick={ModalHandler}>
        판매 상세
      </Plus>
    </CardContainer>
  );
}

export default IsSellingCard;

const CardContainer = styled.article`
  position: relative;
  width: 100%;
  border-radius: 5px;
  overflow: hidden;
  transition: all 0.3s ease-in;
  &:hover {
    transform: scale(1.03);
  }
  > div {
    width: 100%;
    padding-top: 75%;
    position: relative;
    a {
      &:first-child {
        > div {
          width: 100%;
          height: 100%;
          position: absolute;
          left: 0;
          top: 0;
          > picture {
            position: relative;
            width: 100%;
            height: 100%;
            > img {
              position: relative;
              width: 100%;
              height: 100%;
            }
          }
        }
      }
      &:last-child {
        position: absolute;
        bottom: 20px;
        right: 5%;
        width: 15%;
        border: 2px solid transparent;
        overflow: hidden;
        border-radius: 50%;
        background-image: ${(props) =>
          `linear-gradient(#fff, #fff), linear-gradient(to right, ${props.theme.colors.mainMint} 0%, ${props.theme.colors.subMint} 100%)`};
        background-origin: border-box;
        background-clip: content-box, border-box;
        > div {
          position: relative;
          width: 100%;
          padding-top: 100%;
          picture {
            position: absolute;
            left: 0;
            top: 0;
            height: 100%;
            width: 100%;
            img {
              width: 100%;
              height: 100%;
            }
          }
        }
      }
    }
  }
`;

const AuctionInfoContainer = styled.div`
  position: absolute;
  width: 100%;
  height: 33%;
  background: linear-gradient(
    1.15deg,
    #0d0c0f 1.06%,
    rgba(13, 12, 15, 0.73) 54.67%,
    rgba(13, 12, 15, 0) 99.1%
  );
  left: 0;
  bottom: 0;
  padding: 5%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  > div {
    height: 100%;
    width: 80%;
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    h4 {
      font-weight: bold;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 20px;
    }
  }
`;

const AcutionTime = styled.p`
  position: absolute;
  right: 5%;
  top: -5%;
  font-size: 12px;
`;

const AuctionStatus = styled.div`
  position: absolute;
  display: flex;
  justify-content: center;
  width: 25%;
  height: 25%;
  right: 5%;
  top: -180%;
  border-radius: 10px;
  color: ${(props) => props.theme.colors.white};
  background-color: ${(props) =>
    props.auctionstrkey === 0
      ? 'green'
      : props.auctionstrkey === 1
      ? 'orange'
      : props.auctionstrkey === 2
      ? 'orange'
      : props.auctionstrkey === 3
      ? 'red'
      : 'black'};
`;

const Plus = styled.span`
  position: absolute;
  border-radius: 5px;
  padding: 5px;
  bottom: 5%;
  right: 35%;
  background-color: ${(props) => props.theme.colors.mainBlack};
  color: ${(props) => props.theme.colors.subMint};
  border: 1px solid ${(props) => props.theme.colors.subMint};
`;