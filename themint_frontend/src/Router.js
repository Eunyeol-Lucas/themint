import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import NavigationBar from './components/ui/common/NavigationBar';
import Footer from './components/ui/common/Footer';
import ProtectedRoute from './components/routes/ProtectedRoute';
import {
  AccountsEdit,
  AccountsPassword,
  AccountsPhoneNumber,
  AccountsWithdrawl,
  AuctionCreate,
  AuctionDetail,
  Category,
  Login,
  Main,
  NotFound,
  PasswordReset,
  PurchaseHistoryDetail,
  Profile,
  ProfileReviews,
  ProfileSalesHistory,
  ProfilePurchaseHistory,
  ProfileInterest,
  Register,
  StandBy,
  Streaming,
  Talks,
  Accounts,
  Search,
  SearchAuction,
  SearchProduct,
  SearchProfile,
  TalkRoom,
} from './Routes/index';
import { useRecoilValue } from 'recoil';
import { loggedinState } from './atoms';
import { useLayoutEffect } from 'react';

const Wrapper = ({ children }) => {
  const location = useLocation();
  useLayoutEffect(() => {
    document.documentElement.scrollTo(0, 0);
  }, [location.pathname]);
  return children;
};

function Router() {
  const loggedin = useRecoilValue(loggedinState);

  return (
    <BrowserRouter>
      <NavigationBar />
      <Routes>
        <Route path="" element={<Main />} />
        <Route element={<ProtectedRoute loggedin={!loggedin} />}>
          <Route path="login" element={<Login />} />
          <Route path="register" element={<Register />} />
          <Route path="help/password" element={<PasswordReset />} />
        </Route>
        <Route element={<ProtectedRoute loggedin={loggedin} />}>
          <Route path="profile/:userId/*" element={<Profile />}>
            <Route path="" element={<ProfileReviews />} />
            <Route path="profile/:userId/saleshistory" element={<ProfileSalesHistory />} />
            <Route path="profile/:userId/purchasehistory" element={<ProfilePurchaseHistory />} />
            <Route path="profile/:userId/interest" element={<ProfileInterest />} />
          </Route>
          <Route path="accounts" element={<Accounts />}>
            <Route path="edit" element={<AccountsEdit />} />
            <Route path="password" element={<AccountsPassword />} />
            <Route path="phone-number" element={<AccountsPhoneNumber />} />
            <Route path="withdrawl" element={<AccountsWithdrawl />} />
          </Route>
          <Route path="talks" element={<Talks />}>
            <Route path=":roomId" element={<TalkRoom />} />
          </Route>
          <Route path="puchase-history/:purchaseId" element={<PurchaseHistoryDetail />} />
          <Route path="standby/:auctionId" element={<StandBy />} />
          <Route path="auctions/new" element={<AuctionCreate />} />
          <Route path="streamings/:auctionId" element={<Streaming />} />
        </Route>
        <Route path="main" element={<Main />} />
        <Route path="categories/:categoryId" element={<Category />} />
        <Route path="auctions/:auctionId" element={<AuctionDetail />} />
        <Route path="search" element={<Search />} />
        {/* <Route path="" element={<SearchAuction />} />
          <Route path="search" element={<SearchAuction />} />
          <Route path="" element={<SearchAuction />} />
        </Route> */}
        <Route path="*" element={<NotFound />} />
      </Routes>
      <Footer />
    </BrowserRouter>
  );
}

export default Router;
