package spo.tis.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import lombok.Data;

@Data
public class Paging2VO {
	//����¡ ó������ ������Ƽ
	private int cpage2;//���� ������ ������ ��ȣ
	private int pageSize;//�� �������� ������ ��� ����
	private int totalCount;//�� �Խñ� ��
	private int pageCount;//������ ��
	
	//DB���� ���ڵ带 ������� ���� ������Ƽ
	private int start;
	private int end;
	
	//����¡ �� ó�� ���� ������Ƽ
	private int pagingBlock=5;//�� ���� ������ ��������
	private int prevBlock; //���� 5��
	private int nextBlock; //���� 5��
	
	//�˻� ���� ������Ƽ
	private String findType;//�˻� ����
	private String findKeyword;//�˻���
	
	//����¡ ���� ������ �����ϴ� �޼ҵ�
	public void init(HttpSession ses) {
		if(pageSize<0) {
			pageSize=3;
		}
		if(pageSize==0) {
			//�Ķ���ͷ� pageSize�� �Ѿ���� �ʴ´ٸ� ���ǿ��� ������
			Integer ps=(Integer)ses.getAttribute("pageSize");
			if(ps==null) {
				pageSize=3;
			}else {
				pageSize=ps;
			}
		}//if----------
		ses.setAttribute("pageSize", pageSize);
		
		pageCount=(totalCount-1)/pageSize+1;
		if(cpage2<1) {
			cpage2=1;
		}
		if(cpage2>pageCount) {
			cpage2=pageCount;
		}
		end= cpage2 * pageSize;
		start = end -pageSize;
		
		//����¡ �� ����
		prevBlock =(cpage2-1)/pagingBlock * pagingBlock;
		nextBlock = prevBlock + (pagingBlock+1);
		/*cpage
		 * [1][2][3][4][5] | [6][7][8][9][10] | [11][12]...
		 * */
		
	}
	
	/**������ �׺���̼� ���ڿ��� ��ȯ�ϴ� �޼ҵ�*/
	public String getPageNavi(String myctx, String loc) {
			//myctx : ���ؽ�Ʈ��
			//loc   : �Խ��� ��� ���  "/board/list"
			//qStr  : Query String
		  findType=(findType==null)?"": findType;
		  try {
			findKeyword=(findKeyword==null)?"":
			URLEncoder.encode(findKeyword,"UTF-8");
		   } catch (UnsupportedEncodingException e) {
		   }
			
		  String qStr="?pageSize="+pageSize+"&findType="+findType+"&findKeyword="+findKeyword;
		  //String�� �Һ���(immutability) ������ StringBuffer/StringBuilder
		  //�� �̿��Ͽ� ���ڿ��� ������ �Ŀ� String���� ����� ��ȯ����.
		  StringBuilder buf=new StringBuilder()
				  .append("<ul class='pagination'>");
				  if(prevBlock>0) {
					  //���� 5��
				buf.append("<li class='page-item'><a class='page-link' href='"+myctx+"/"+loc+qStr+"&cpage="+prevBlock+"'>");
					  buf.append("Prev");
					  buf.append("</a></li>");
				  }
				  for(int i=prevBlock+1;i<=nextBlock-1 && i<=pageCount;i++) {
					  String css="";
					  if(i==cpage2) {
						  css="active";
					  }else {
						  css="";
					  }					  
					  
					  buf.append("<li class='page-item "+css+"'><a class='page-link' href='"+myctx+"/"+loc+qStr+"&cpage2="+i+"'>");
					  buf.append(i);
					  buf.append("</a></li>");
				  }//for--------
				  
				  if(nextBlock<pageCount) {
						//���� 5��
						buf.append("<li class='page-item'><a class='page-link' href='"+myctx+"/"+loc+qStr+"&cpage2="+nextBlock+"'>");
						buf.append("Next");
						buf.append("</a></li>");
					}
				  
				  buf.append("</ul>");
		  String str=buf.toString();	
		  		//System.out.println(str);
		return str;
	}
	
	
	
}








