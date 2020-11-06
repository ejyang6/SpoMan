package spo.tis.mapper;

import java.util.List;

import spo.tis.domain.McVO;
import spo.tis.domain.MtVO;
import spo.tis.domain.Paging2VO;
import spo.tis.domain.Paging3VO;
import spo.tis.domain.PagingVO;

public interface MyDataMapper {
   
   List<McVO> clubList(PagingVO paging);
   int clubCount(PagingVO paging);
   
   List<McVO> clubHList(Paging2VO paging);
   int clubHCount(Paging2VO paging);
   
   List<MtVO> teamList(Paging3VO paging);
   int teamCount(Paging3VO paging);
   

}