package com.sureprompt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitingFilterTest {

    @Test
    void blocksAfterBucketIsExhaustedForSameIp() throws Exception {
        TestableRateLimitingFilter filter = new TestableRateLimitingFilter();
        FilterChain okChain = (request, response) -> ((HttpServletResponse) response).setStatus(200);

        for (int i = 0; i < 100; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/feed");
            request.setRemoteAddr("10.10.10.10");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.invoke(request, response, okChain);
            assertThat(response.getStatus()).isEqualTo(200);
        }

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/feed");
        request.setRemoteAddr("10.10.10.10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.invoke(request, response, okChain);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("Too many requests");
    }

    @Test
    void doesNotRateLimitNonApiPaths() throws Exception {
        TestableRateLimitingFilter filter = new TestableRateLimitingFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain okChain = (req, res) -> ((HttpServletResponse) res).setStatus(204);
        filter.invoke(request, response, okChain);

        assertThat(response.getStatus()).isEqualTo(204);
    }

    private static final class TestableRateLimitingFilter extends RateLimitingFilter {
        void invoke(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            super.doFilterInternal(request, response, chain);
        }
    }
}
