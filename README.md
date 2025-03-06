# 🛵 배달의 민족 어플리케이션
이 프로젝트는 `Spring Boot`와 `MySQL`을 사용하여 개발된 배달 관리 어플리케이션 입니다.<br>
`JWT` 방식의 인증 방법을 사용하고 있으며, `JPA`를 활용한 `MySQL DB`를 사용하고 있습니다.<br>
각종 상황에 따른 예외 처리를 제공하고 있습니다.<br>


![Java](https://img.shields.io/badge/Java-17-blue?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-green?style=for-the-badge&logo=springboot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate%20ORM-%236DB33F?style=for-the-badge&logo=hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-%233A3A3A?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-6.6.5.Final-%236DB33F?style=for-the-badge&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange?style=for-the-badge&logo=mysql&logoColor=white)
[![GitHub](https://img.shields.io/badge/GitHub-Organization-black?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Tenstagram)

## 📌 주요 기능
### 1. 사용자 관리
   - **회원가입**: 새로운 사용자를 등록합니다.
   - **로그인**: 사용자 인증을 위한 JWT 기반 로그인입니다.
   - **사용자 정보 조회**: 사용자 정보를 확인할 수 있습니다.
   - **사용자 비밀번호 수정**: 사용자 비밀번호를 업데이트할 수 있습니다.
   - **사용자 역할 수정**: 사용자 역할을 업데이트할 수 있습니다.
   - **사용자 탈퇴**: Soft Delet 방식의 회원 탈퇴 기능입니다.
### 2. 가게 관리  
   - **가게 생성**: 새로운 가게를 생성합니다. 사장만 생성 가능하며, 1명당 최대 3개까지 생성 가능합니다.
   - **가게 전체 조회**: 필터링 및 페이징을 지원하는 가게 목록 조회기능 입니다.
   - **특정 가게 조회**: 선택한 가게의 상세 정보와 해당 가게의 메뉴 리스트를 조회할 수 있습니다.
   - **특정 가게 수정**: 선택한 가게의 정보를 수정할 수 있습니다.
   - **특정 가게 폐업**: Soft Delet 방식의 가게 폐업 기능입니다.
### 3. 메뉴 관리
   - **메뉴 생성**: 게시글에 댓글을 추가합니다.
   - **메뉴 조회**: 특정 가게의 메뉴를 조회할 수 있습니다.
   - **특정 메뉴 수정**: 선택한 메뉴의 상세 정보를 수정할 수 있습니다.
   - **특정 메뉴 삭제**: Soft Delet 방식의 메뉴 삭제 기능입니다.
### 4. 장바구니 기능
   - **장바구니 추가 및 수정**: 특정 가게의 특정 메뉴를 장바구니에 추가 및 수정할 수 있습니다.
   - **장바구니 조회**: 자신이 등록한 장바구니를 조회할 수 있습니다.
   - **장바구니 삭제**: Soft Delet 방식의 장바구니 삭제 기능입니다. 매일 00:00시에 Soft Delet된지 일정 시간이 지난 데이터는 Hard Delet됩니다.
### 5. 주문 기능
   - **주문하기**: 특정 장바구니의 항목을 주문할 수 있습니다.
   - **주문 목록 조회**: 주문 목록을 조회할 수 있습니다.
   - **주문 상세 조회**: 특정 주문 사항을 상세 조회할 수 있습니다.
   - **주문 현황 수정**: 특정 주문의 현황을 수정할 수 있습니다.
### 6. 리뷰 기능
   - **고객 리뷰 추가**: 주문 완료된 항목에 대해 고객이 리뷰를 남길 수 있습니다.
   - **사장 리뷰 추가**: 고객이 남긴 리뷰에 대해 사장이 답변 리뷰를 남길 수 있습니다.
   - **리뷰 전체 조회**: 특정 가게의 전체 리뷰를 조회할 수 있습니다.
   - **리뷰 수정** : 특정 리뷰의 내용을 수정할 수 있습니다.
   - **리뷰 삭제**: Soft Delet 방식의 리뷰 삭제 기능입니다.
### 7. 가게 즐겨찾기 기능
   - **즐겨찾기 추가**: 특정 가게에 대해 즐겨찾기를 추가할 수 있습니다.
   - **즐겨찾기 가게 전체 조회**: 즐겨찾기한 가게를 전체 조회할 수 있습니다.
   - **즐겨찾기 삭제**: Soft Delet 방식의 즐겨찾기 삭제 기능입니다. 즐겨찾기한 가게를 즐겨찾기 해제할 수 있습니다.

## 🛠️ 기술 스택
### Backend
- Java 17+
- Spring Boot 3.4.2
- Spring Data JPA
- JPA Auditing

### Database
- MySQL 8.0

### Security
- JWT (JSON Web Token)
- BCrypt (`at.favre.lib:bcrypt:0.10.2`)

### Utilities
- Lombok


## 📆 개발 기간
2025-02-28 ~ 2025-03-07


## 🖼️ 와이어프레임
<details>
  <summary>1.메인화면</summary>
  
  ![image](https://github.com/user-attachments/assets/18345db2-fd98-48f3-b6fb-cb4275b4272e)

</details>

<details>
  <summary>2.회원가입</summary>
  
  ![image](https://github.com/user-attachments/assets/f12c3e67-4281-4efa-bdc7-4f800df76821)

</details>

<details>
  <summary>3.로그인</summary>
  
  ![image](https://github.com/user-attachments/assets/c5361e86-721d-4f6f-a88b-b77dcefcaa14)


</details>

<details>
  <summary>4.고객 프로필 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/fe20bfcf-049e-4545-8472-6c856e0f66d1)


</details>

<details>
  <summary>5.사장 프로필 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/44e93c4d-5d51-49f8-92b4-c0c36ab68cbf)


</details>

<details>
  <summary>6.식당 추가 및 삭제 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/6c29f452-5b83-4f2a-8011-bb2b40c4dcb0)


</details>

<details>
  <summary>7.식당 내의 메뉴 추가 및 식당 상태 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/88a8877a-24ac-4ad5-96cd-39c8f6a0bef2)

</details>

<details>
  <summary>8.검색 시 식당이 나타나는 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/7c06cdb2-b4a8-4993-9946-9ba1ec6060df)

</details>

<details>
  <summary>9.고객이 식당 선택 시 나타나는 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/c5a4bfbd-cfae-4bdd-8dfc-62bef412c2d8)

</details>

<details>
  <summary>10.리뷰 화면</summary>
  
  ![image](https://github.com/user-attachments/assets/056508c6-92cc-4243-9fdd-0b60181150ac)

</details>





## 💡 ERD
![image](https://github.com/user-attachments/assets/71be1090-e1b1-42a4-bb01-29187541d210)



## 📖 API 명세
자세한 API 명세는 API 문서를 통해 확인할 수 있습니다.

[API 문서 보기](https://www.notion.so/teamsparta/1a82dc3ef514817aa690cea85cbd38ef?v=1a82dc3ef514806281af000c9014617d&pvs=4)

## 📺 시연 영상
프로젝트 시연 영상은 영상 링크를 통해 확인할 수 있습니다.

[시연 영상 보기]()

