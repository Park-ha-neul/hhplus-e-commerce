### ERD 설계
![ERD 설계 이미지](./img/ERD%20설계.png)

<details>
    <summary>테이블에 대한 자세한 설명</summary>

```
Table user_point{
  user_id integer [primary key] // 사용자 id 
  point integer // 보유 포인트
  create_date timestamp // 등록일 
  update_date timestamp // 수정일 
}

Table point_history{
  history_id integer [primary key] // 이력 id
  user_id integer [ref: > user_point.user_id] // 사용자 id
  amount integer // 변동 포인트
  balance_before integer // 변경 전 포인트 잔액
  balance_after integer // 변경 후 포인트 잔액 
  transaction_type varchar // 거래 유형 (예: 적립, 사용 등)
  create_date timestamp // 이력 생성일 
}

Table product{
  product_id integer [primary key] // 상품 id
  name varchar // 상품명
  description text // 상품 설명 
  stock integer // 재고
  price integer // 가격 
  status varchar // 상품 상태 (예: 판매중, 품절)
  create_date timestamp // 등록일
  update_date timestamp // 수정일 
}

Table user_order{
  order_id integer [primary key] // 주문 id
  user_id integer [ref: > user_point.user_id] // 사용자 id
  create_date timestamp // 주문 생성일
}

Table order_detail{
  order_detail_id integer [primary key]// 주문 상세 id
  order_id integer [ref: > user_order.order_id] // 주문 id
  product_id integer [ref: > product.product_id] // 상품 id
  quantity integer // 주문한 상품 갯수 
  unit_price integer // 주문 당시 개별 상품 가격
}

Table payment{
  payment_id integer [primary key]// 결제 id 
  order_id integer [ref: > user_order.order_id] // 주문 id
  total_amount integer // 총 결제 금액  
  status varchar // 결제 상태 (예: 성공, 실패, 대기)
  create_date timestamp // 결제 일자  
}

Table payment_detail{
  payment_detail_id integer [primary key] // 결제 상세 id
  payment_id integer [ref: > payment.payment_id] // 결제 id 
  product_id integer [ref: > product.product_id] // 상품 id 
  quantity integer // 구매한 상품 개수 
}

// 할인율과 금액 할인은 동시에 존재할 수 없습니다. 
// 할인율이 존재하면 금액 할인은 NULL이어야 하고, 그 반대도 마찬가지입니다.
Table coupon{
  coupon_id integer [primary key] // 쿠폰 id
  coupon_name varchar // 쿠폰 명 
  total_quantity integer // 쿠폰의 총 발급 수량 (선착순)
  issued_quantity integer // 실제 발급된 수량 
  discount_rete integer // 할인율
  discount_amount integer // 금액 할인 (예: 5000원 할인)
  status varchar // 쿠폰 상태 (예: 활성, 종료)
  start_date timestamp // 쿠폰 발급 시작일 
  end_date timestamp // 쿠폰 발급 종료일 
  create_date timestamp // 쿠폰 등록일
  update_date timestamp // 쿠폰 수정일 
}

Table coupon_detail{
  coupon_detail_id integer [primary key] // 쿠폰 상세 id 
  user_id integer [ref: > user_point.user_id] // 사용자 id 
  coupon_id integer [ref: > coupon.coupon_id] // 쿠폰 id 
}

```

</details>