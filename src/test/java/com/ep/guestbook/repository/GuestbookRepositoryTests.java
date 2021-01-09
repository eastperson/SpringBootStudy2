package com.ep.guestbook.repository;

import com.ep.guestbook.entity.Guestbook;
import com.ep.guestbook.entity.QGuestbook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Test
    public void insertDummies(){

        IntStream.rangeClosed(1,300).forEach(i -> {

            Guestbook guestbook = Guestbook.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer("user" + (i % 10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));

        });

    }

    @Test
    public void upadteTest(){

        Optional<Guestbook> result = guestbookRepository.findById(300L); // 존재하는 번호로 테스트

        if(result.isPresent()) {

            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title...");
            guestbook.changeContent("Changed Title..");

            guestbookRepository.save(guestbook);

        }
    }

    @Test
    public void testQuery1() {
        Pageable pageable = PageRequest.of(0,10, Sort.by("gno").descending());

        // 엔티티에 선언된 필드들을 변수로 싸용 가능
        QGuestbook qGuestbook = QGuestbook.guestbook; // 1

        // 검색할 키워드
        String keyword = "1";

        // 쿼리문으로 치면 where 절에 들어가는 내용이다.
        BooleanBuilder builder = new BooleanBuilder(); // 2

        // 원하는 조건과 필드 값을 결합해서 생성한다.
        // 원하는 조건은 Java에 있는 Predicate 타입이 아닌, com.querydsl.core.types.Predicate 타입이어야 한다.
        BooleanExpression expression = qGuestbook.title.contains(keyword); // 3

        // 만들어진 조건(expression)은 and 문 등으로 추가를 하낟.
        builder.and(expression); // 4

        // pagable은 predicate 함수이다.
        Page<Guestbook> result = guestbookRepository.findAll(builder,pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

    @Test
    public void testQuery2(){

        // 페이징 처리 gno로 내림차순
        Pageable pageable = PageRequest.of(0,10,Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();

        // 첫 번째 조건
        // 제목에 keyword가 있는지
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);

        // 두 번째 조건
        // 내용에 keyword가 있는지
        BooleanExpression exContent = qGuestbook.content.contains(keyword);

        // 두 조건을 or로 결합
        BooleanExpression exAll = exTitle.or(exContent);

        // builder에 포함
        builder.and(exAll);

        // gno가 0보다 큰지
        builder.and(qGuestbook.gno.gt(0L));

        Page<Guestbook> result = guestbookRepository.findAll(builder,pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });

    }

}
