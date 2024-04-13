package com.overcomingroom.bellbell.lunch.service;

import com.overcomingroom.bellbell.lunch.domain.entity.Menu;
import com.overcomingroom.bellbell.lunch.repository.MenuRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final EntityManager entityManager;

    @Value("classpath:csv/menu.csv")
    private Resource resource;

    @PostConstruct
    private void initMenu() {

        List<Menu> menuList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line = "";

            while ((line = br.readLine()) != null) {
                Menu menu = new Menu(line);
                menuList.add(menu);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 데이터 중복 방지
        menuRepository.deleteAll();
        menuRepository.saveAll(menuList);
    }

    // 랜덤으로 메뉴를 선정함.
    public List<Menu> recommendMenus() {
        TypedQuery<Menu> query = entityManager.createQuery(
            "SELECT m FROM Menu m ORDER BY FUNCTION('RAND')", // JPQL 사용
            Menu.class
        );

        query.setMaxResults(3); // 랜덤하게 선택할 메뉴 개수 지정
        return query.getResultList();
    }
}
