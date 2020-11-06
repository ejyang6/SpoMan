package spo.tis.index;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import lombok.extern.log4j.Log4j;
import spo.tis.common.util.CommonUtil;
import spo.tis.domain.CPictureVO;
import spo.tis.domain.ClubVO;
import spo.tis.domain.UserVO;
import spo.tis.service.CPictureService;
import spo.tis.service.ClubService;

@Controller
@RequestMapping("/club")
@Log4j
public class ClubPictureController {

	@Autowired
	private ClubService ClubSvc;

	@Autowired
	private CPictureService CPictureSvc;

	@Inject
	private CommonUtil util;
	
	@RequestMapping("/photo")
	public String clubPhoto(Model model, @RequestParam(defaultValue = "0") int cno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		// ���ӹ�ȣ�� ���� ���� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		// ���ӹ�ȣ�� Ư�� ������ ������ ��������
		List<CPictureVO> cpList = CPictureSvc.getClubPicture(cno);

		// req�� ����
		model.addAttribute("cpList", cpList);
		model.addAttribute("clubView", clubView);
		model.addAttribute("cno", cno);

		return "/club/clubPhoto";
	}

	
	
	
	@RequestMapping("/photoAdd")
	public String clubPhotoAdd(Model model, @RequestParam(defaultValue = "0") int cno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		// ���ӹ�ȣ�� ���� ���� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		model.addAttribute("cno", cno);
		model.addAttribute("clubView", clubView);

		return "/club/clubphotoAdd";
	}

	
	
	
	
	@RequestMapping("/photoAddEnd")
	public String clubPhotoAddEnd(Model model, HttpServletRequest req,
			@RequestParam(defaultValue = "0") int cno,
			@ModelAttribute("cpicture") CPictureVO cpicture,
			@RequestParam("mfilename") MultipartFile mfilename) {

		//�� �޾ƿ���
		String cptitle = cpicture.getCptitle();
		
		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}
		
		if(cptitle==null || cptitle.trim().isEmpty() || mfilename==null) {
			return util.addMsgBack(model, "��������� ������ �Է����ּ���.");
		}
		
		//���ε��� ���丮�� ������ ������
		ServletContext ctx = req.getServletContext();
		String upDir = ctx.getRealPath("/clubphotoimage");
		log.info("upDir===" + upDir);
		
		File dir=new File(upDir);
		if(! dir.exists()) {
			dir.mkdirs();//���丮 ����
		}
		//������ ÷���ߴٸ�
		if(!mfilename.isEmpty()) {
			
			//÷�����ϸ�, ���� ũ�⸦ �˾Ƴ���.
			String fname=mfilename.getOriginalFilename();//�������ϸ�
			
			//÷�������� �̹� �����ϴ� ������ ��� ����⸦ �����ϱ� ����
			UUID uuid=UUID.randomUUID();//������ ���ڿ��� �߻���Ű�� ���� UUID��ü�� ������.
			String str=uuid.toString();
			String fname2=str+"_"+fname;//���������ϸ�
			
			cpicture.setCpimage(fname2); ;//���������ϸ�

			//���� ���ε� ó��==> transferTo()�� �̿��ؼ� ���ε� ó��			
			try {
				///////////////////////////////////
				mfilename.transferTo(new File(upDir,fname2));
				////////////////////////////////
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//ȸ����ȣ ��������
		HttpSession ses=req.getSession();
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");
		
		//���ӹ�ȣ
		cpicture.setCno(cno);
		
		if(uvo!=null) {
			
			cpicture.setIdx(uvo.getIdx());
			
			int n=CPictureSvc.insertPhoto(cpicture);
			
			//���ε� ���� >����ø �̵�, ����>�ڷΰ���
			String msg=(n>0)? "���� ��� ����":"���� ��� ����";
			String loc=(n>0)? "photo?cno="+cno:"javascript:history.back()"; 
			
			return util.addMsgLoc(model, msg, loc);
		}
		
		return util.addMsgBack(model, "�α����� �ؾ��ؿ�.");
	}

	
	
	@RequestMapping("/photoMy")
	public String clubPhotoMy(Model model, HttpSession ses, @RequestParam(defaultValue = "0") int cno) {

		// ��ȿ�� üũ => list�� redirect�̵�
		if (cno == 0) {
			return "redirect:list";
		}

		// �� ���� ��������
		UserVO uvo = (UserVO) ses.getAttribute("loginUser");

		if (uvo != null) {
			// ���� ����ø ��������
			CPictureVO cpvo = new CPictureVO();
			cpvo.setCno(cno);
			cpvo.setIdx(uvo.getIdx());

			List<CPictureVO> cpList = CPictureSvc.getMyPicture(cpvo);

			model.addAttribute("cpList", cpList);
		}

		// ���ӹ�ȣ�� ���� ���� ��������
		ClubVO clubView = ClubSvc.viewClub(cno);

		model.addAttribute("cno", cno);
		model.addAttribute("clubView", clubView);

		return "/club/clubphotoMy";
	}

	
	
	
	@RequestMapping("/photoDel")
	public String clubPhotoDel(Model model, HttpSession ses, 
			@RequestParam(defaultValue = "0") int cno,
			@RequestParam(defaultValue = "0") int cpno) {
		
		// ��ȿ�� üũ
		if (cno == 0) {
			return util.addMsgBack(model, "�ش��ϴ� ������ �����ϴ�.");
		}
		if (cpno == 0) {
			return util.addMsgBack(model, "������ �׸��� �����ϴ�.");
		}

		int n = CPictureSvc.deletePhoto(cpno);

		String msg = (n > 0) ? "���� ����" : "���� ����";
		String loc = "photoMy?cno=" + cno;

		return util.addMsgLoc(model, msg, loc);
	}
}
