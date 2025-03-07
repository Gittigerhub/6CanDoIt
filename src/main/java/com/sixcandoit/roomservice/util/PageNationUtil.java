package com.sixcandoit.roomservice.util;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 페이지 처리에 필요한 메소드를 담은 클래스
@Component
public class PageNationUtil {

    public static Map<String, Integer> Pagination(Page<?> page) {
        Map<String, Integer> map = new HashMap<>();            // 결과를 저장할 변수
        
        int currentPage = page.getNumber()+1;                  // 현제페이지 번호
        int blockLimit = 5;                                    // 화면에 출력할 페이지번호의 수(1,2,3,4,...,10)

        // Math.max(값,값,값,...) : 값들 중 최대값을 추출
        // 계산된 페이지번호가 1보다 작으면 1을적용, 크면 해당페이지번호를 시작페이지 번호로 적용
        int startPage = Math.max(1, currentPage-blockLimit/2); // 화면에 시작하는 번호

        // 계산된 끝페이지 번호가 전체페이지 번호보다 크면 전체페이지
        int endPage = page.getTotalPages();                     // 마지막 페이지 번호
        
        // 이전/다음 페이지 번호
        int prevPage = Math.max(1, currentPage-1);              // 계산된 페이지가 1보다 작으면 1페이지를 적용
        int nextPage = Math.min(endPage, currentPage+1);
        
        // 처음(1) 이전(prevPage) 1(startPage) 2 3(currentPage) 4 5(endPage) 다음(nextPage) 마지막(lastPage)
        map.put("prevPage", prevPage);
        map.put("startPage", startPage);
        map.put("currentPage", currentPage);
        map.put("endPage", endPage);
        map.put("nextPage", nextPage);
        
        return map;

    }
}