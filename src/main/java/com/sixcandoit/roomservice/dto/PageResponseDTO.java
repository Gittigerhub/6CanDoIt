package com.sixcandoit.roomservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;  //게시물 총 수, 검색시 총 수량이 변경됨

    //시작 페이지 번호
    private int start;

    //끝 페이지 번호
    private int end;

    //이전 페이지 존재 여부
    private boolean prev;

    //다음페이지 존재 여부
    private boolean next;

    private List<E> dtoList;        //목록에 대한 결과 값, 제너럴 사용으로 다른 곳에서도 사용 가능

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {

        if (total <= 0) {
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.end = (int) (Math.ceil(this.page / 10.0)) * 10;

        this.start = this.end - 9;

        int last = (int)(Math.ceil(total / (double)size));

        this.end = end > last ? last : end;

        this.prev = this.start > 1;

        this.next = total > this.end * this.size;

    }
}
