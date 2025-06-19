import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export let errorCount = new Counter('errors');

export let options = {
  stages: [
    { duration: '2m', target: 100 },
    { duration: '2m', target: 200 },
    { duration: '2m', target: 300 },
    { duration: '2m', target: 400 },
    { duration: '2m', target: 500 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<1500'], // max 1s
    errors: ['count<100'],
  },
};

const BASE_URL = 'http://host.docker.internal:8080'; // target your local server
const COUPON_IDS = [1, 2, 3, 4, 5];
const ORDER_IDS = [1, 2, 3, 4, 5];
const PRODUCT_IDS = [1, 2, 3, 4, 5];

export default function () {
  const userId = Math.floor(Math.random() * 10000);
  const periodType = 'DAILY';
  const limit = 5;
  const randomCouponId = COUPON_IDS[Math.floor(Math.random() * COUPON_IDS.length)];
  const randomOrderId = ORDER_IDS[Math.floor(Math.random() * ORDER_IDS.length)];
  const randomProductId = PRODUCT_IDS[Math.floor(Math.random() * PRODUCT_IDS.length)];
  const pointRequest = {
      userId: userId,
      point: Math.floor(Math.random() * 10000)
  };
  const orderRequest = {
    userId: userId,
    userCouponId: randomCouponId,
    orderItems: [
      { productId: 1, quantity: 2, price: 1000 },
      { productId: 2, quantity: 1, price: 2000 }
    ]
  };
  const paymentRequest = {
      orderId: randomOrderId,
      totalAmount: 500
  };
  const endpoints = [
    {
      name: 'Popular Products',
      req: () => http.get(`${BASE_URL}/products/popular?periodType=${periodType}&limit=${limit}`),
    },
    {
      name: 'Product Detail',
      req: () => http.get(`${BASE_URL}/products/${randomProductId}`),
    },
    {
      name: 'Issue Coupon',
      req: () => http.post(`${BASE_URL}/users/${userId}/coupons`, null),
    },
    {
      name: 'Charge Point',
      req: () => http.post(`${BASE_URL}/points/charge`, JSON.stringify(pointRequest), { headers: { 'Content-Type': 'application/json' } }),
    },
    {
      name: 'Order + Payment',
      req: () => {
        const orderRes = http.post(`${BASE_URL}/orders`, JSON.stringify(orderRequest), { headers: { 'Content-Type': 'application/json' } });
        if (orderRes.status === 201) {
          const orderId = JSON.parse(orderRes.body).orderId || '1';
          return http.post(`${BASE_URL}/payments`, JSON.stringify(paymentRequest), { headers: { 'Content-Type': 'application/json' } });
        }
        return orderRes;
      },
    },
  ];

  const selected = endpoints[Math.floor(Math.random() * endpoints.length)];
  const res = selected.req();

  const success = check(res, {
    'status is 2xx or 3xx': (r) => r.status >= 200 && r.status < 400,
    'duration < 1500ms': (r) => r.timings.duration < 1500,
  });

  if (!success) errorCount.add(1);
  sleep(1);
}