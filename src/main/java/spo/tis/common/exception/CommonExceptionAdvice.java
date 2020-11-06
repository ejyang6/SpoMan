package spo.tis.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j;
import spo.tis.domain.NotUserException;
/* ��翹�ܸ� ó���ϴ� �������Ѵ�.
 * @ControllerAdvice������̼��� ���̸� �� Ŭ������ ��ü�� 
 * ��Ʈ�ѷ����� �߻��ϴ� ���ܸ� ���������� ó���ϴ� Ŭ�������� ����ϴ� ���̴�.
 * */
@ControllerAdvice
@Log4j
public class CommonExceptionAdvice {//Advice�� ������ɻ�
	
	@ExceptionHandler(NotUserException.class)
	public String commonLoginError(NotUserException ex) {
		System.out.println("�α��� ó���� ����"+ex.getMessage());
		return "member/errorAlert";
	}
	
	@ExceptionHandler(Exception.class)
	public String commonError(NotUserException ex) {
		System.out.println("����"+ex.getMessage());
		//ex.printStackTrace();
		return "member/errorAlert";
	}
	
	
}
