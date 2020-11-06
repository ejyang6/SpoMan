package spo.tis.service;

import java.util.List;

import spo.tis.domain.McVO;
import spo.tis.domain.MtVO;
import spo.tis.domain.Paging2VO;
import spo.tis.domain.Paging3VO;
import spo.tis.domain.PagingVO;

public interface MyDataService {

   int clubCount(PagingVO paging);
   List<McVO> clubList(PagingVO paging);
   
   int clubHCount(Paging2VO paging);
   List<McVO> clubHList(Paging2VO paging);

   int teamCount(Paging3VO paging);
   List<MtVO> teamList(Paging3VO paging);
   
   
   
}