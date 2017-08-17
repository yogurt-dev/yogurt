package pub.filter;


import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Administrator on 2015/8/19.
 */
@WebFilter(filterName = "LoginFilter")
public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {


        HttpServletRequest request = (HttpServletRequest) req;

        HttpServletResponse response = (HttpServletResponse) resp;
        String url = request.getRequestURI();
        HttpSession session = request.getSession(true);
        SecurityUserT user = (SecurityUserT)session.getAttribute("user");
        System.out.print(url.endsWith("login.html"));
        if(url.endsWith("login.html") ||url.endsWith("login.do")|| user!=null || url.endsWith("number1.html") || url.endsWith(".css") ||

                url.endsWith(".js")|| url.endsWith(".gif")|| url.endsWith(".png")|| url.endsWith(".jpg")||

                url.endsWith("SSH_market/")){
            chain.doFilter(req,resp);
        }else{
            response.sendRedirect(request.getContextPath()+"/login.html");
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
