package spo.tis.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import spo.tis.domain.NotUserException;
import spo.tis.domain.UserVO;
import spo.tis.mapper.UserMapper;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

	@Inject
	private UserMapper userMapper;
	
	@Override
	public int createUser(UserVO user) {
		return userMapper.createUser(user);
	}
	
	@Override
	public UserVO findUser(UserVO user) {
		
		return this.userMapper.findUser(user);
	}
	
	@Override
	public UserVO loginCheck(UserVO user) throws NotUserException {
		//ȸ�� ���̵�� ȸ�������� ��������
		UserVO tmpUser=this.findUser(user);
		if(tmpUser==null) {
			//���̵� �������� �ʴ� ���
			throw new NotUserException("�������� �ʴ¾��̵��Դϴ�.");//�̷��� ��ü������ �˷��ָ�ȵǴ� �Ž̤�
		}
		if(!tmpUser.getPwd().equals(user.getPwd())) {
			throw new NotUserException("��й�ȣ�� ��ġ���� �ʾƿ�");
		}
		return tmpUser;
	}

}
