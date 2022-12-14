import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { loggedinState, myInformationState } from '../../atoms';
import { infoToast } from '../../lib/toast';
import { removeRefreshToken } from '../apis/api';
import { removeCookie } from '../functions/cookies';

function useLogout() {
  const [myInformation, setMyinformation] = useRecoilState(myInformationState);
  const setLoggedin = useSetRecoilState(loggedinState);
  const navigate = useNavigate();

  return ({ type }) => {
    if (type === 'withdrawl') {
      infoToast('회원 탈퇴에 성공하셨습니다.');
    } else {
      infoToast(`${myInformation.nickname}님 다음에 또 오세요!`);
    }
    setLoggedin(false);
    setMyinformation({ memberId: '', memberSeq: null, nickname: '' });
    removeCookie('accessToken');
    removeRefreshToken();
    navigate('/main');
  };
}

export default useLogout;
