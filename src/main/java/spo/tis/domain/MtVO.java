package spo.tis.domain;

import lombok.Data;

@Data
public class MtVO {
	
	private int mtno; //primary key
	private String bidx; //��Ī �� ��ȣ
	private int hmno; //�۾� ��� ȸ����ȣ
	private int htno; //�۾� ��� ����ȣ
	private int amno; //��û�� ��� ȸ����ȣ
	private int atno; //��û�� ��� ����ȣ

	//�߰�
	private String tname;
	private String game;
	private String addr;
}
