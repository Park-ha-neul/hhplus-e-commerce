import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50,  // 가상 사용자 수
  duration: '30s',  // 테스트 실행 시간
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  const periodType = 'DAILY';

  // 캐시를 미적용한 요청
  const resWithoutCache = http.get(`${BASE_URL}/products/popular?periodType=${periodType}&force=true`);

  check(resWithoutCache, {
    'status is 200 for no cache': (r) => r.status === 200,
    'response has items for no cache': (r) => r.json().length > 0,
  });

  sleep(1); // 1초 대기 후 반복
}

export function handleSummary(data) {
  const duration = data.metrics.http_req_duration.values;

  return {
    'without_cache_result.md': `
# 📊 k6 Load Test Summary (캐시를 사용하지 않은 경우)

## ✅ HTTP 요청 통계
- **요청 수**: ${data.metrics.http_reqs.values.count}
- **평균 응답 시간**: ${duration.avg.toFixed(2)} ms
- **최소 응답 시간**: ${duration.min.toFixed(2)} ms
- **최대 응답 시간**: ${duration.max.toFixed(2)} ms
- **P90 응답 시간**: ${duration['p(90)'].toFixed(2)} ms
- **P95 응답 시간**: ${duration['p(95)'].toFixed(2)} ms

---

> 테스트 대상 URL: \`${BASE_URL}/products/popular?periodType=DAILY&force=true\`
>
> 보고서 생성일: ${new Date().toISOString()}

`,
  };
}