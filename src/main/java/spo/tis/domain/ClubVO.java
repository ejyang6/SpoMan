package spo.tis.domain;

import lombok.Data;

@Data
public class ClubVO {

	private int cno; //���� ��ȣ
	private String cname; //���� �̸�
	private String csports; //����
	private String cking; //������
	private int cnumber; //Ŭ�� �ο���
	private String cplace; //����
	private String cinfo; //���� �Ұ�
	private String chp1; //��ȭ��ȣ
	private String chp2;
	private String chp3;
	private int cstate; //Ȱ������ 
	private String cimage; //���ӻ���(����)
	
	//�߰�
	private int idx; //ȸ����ȣ
	
	@Override
	public String toString() {
		return "ClubVO [cno=" + cno + ", cname=" + cname + ", csports=" + csports + ", cking=" + cking + ", cnumber="
				+ cnumber + ", cplace=" + cplace + ", cinfo=" + cinfo + ", chp1=" + chp1 + ", chp2=" + chp2 + ", chp3="
				+ chp3 + ", cstate=" + cstate + "]";
	}
	
	//start, end
	private int start;
	private int end;
	
	//�˻�
	private String findType; //�˻�����
	private String findKeyword; //�˻�����
	private String sportsType; //������ ���� 
	
	//�˻� ��
	private String findTypeh; //�˻�����
	private String findKeywordh; //�˻�����
	private String sportsTypeh; //������ ���� 
	
	public ClubVO(){  
		
	}
	
	public ClubVO(int cno, String cname, String csports, String cking, int cnumber, String cplace, String cinfo,
			String chp1, String chp2, String chp3, int cstate, String cimage) {
		super();
		this.cno = cno;
		this.cname = cname;
		this.csports = csports;
		this.cking = cking;
		this.cnumber = cnumber;
		this.cplace = cplace;
		this.cinfo = cinfo;
		this.chp1 = chp1;
		this.chp2 = chp2;
		this.chp3 = chp3;
		this.cstate = cstate;
		this.cimage = cimage;
	}

	
	
	public ClubVO(String cname, String csports, String cplace, String cinfo, String chp1, String chp2, String chp3,
			String cimage) {
		super();
		this.cname = cname;
		this.csports = csports;
		this.cplace = cplace;
		this.cinfo = cinfo;
		this.chp1 = chp1;
		this.chp2 = chp2;
		this.chp3 = chp3;
		this.cimage = cimage;
	}

}


