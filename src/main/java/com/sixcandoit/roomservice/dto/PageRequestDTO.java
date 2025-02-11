package com.sixcandoit.roomservice.dto;

import com.sixcandoit.roomservice.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PageRequestDTO {

    @Builder.Default
    private int page = 1; //현재 페이지

    @Builder.Default
    private int size = 10;  //페이지 사이즈(10개씩)

    private String type;    //검색 종류, 셀렉트 박스로
                            // 종류는 추후 추가

    private String keyword; //검색어

    private String link;   //주소

    private String searchDateType;  // all, 1d, 1w, 1m, 6m
                                    // <select name = "searchdatetype">
                                    // <option value="all">전체</option<
                                    // <option value="1d">하루전</option<
                                    // <option value="1w">한달전</option<
                                    // </select>

    private OrderStatus orderStatus;
    private String searchBy;

    public String[] getTypes(){

        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String...props){
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }

    public String getLink() {
        if (link == null){
            StringBuffer sb =  new StringBuffer();

            sb.append("page=" + this.page);
            sb.append("&size=" + this.size);

            if (type != null && type.length() > 0) {
                sb.append("&type=" + type);
            }
            if (keyword != null) {
                try {
                    sb.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e){

                }
            }
            link = sb.toString();
        }
        return link;
    }
}
