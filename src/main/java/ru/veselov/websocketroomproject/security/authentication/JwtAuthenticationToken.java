package ru.veselov.websocketroomproject.security.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private boolean isAuthenticated;

    private Object principal;

    private String jwt;

    public JwtAuthenticationToken(String jwt) {
        super(Collections.emptyList());
        this.isAuthenticated = false;
        this.jwt = jwt;
    }

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities,
                                  Object principal,
                                  boolean isAuthenticated,
                                  String jwt) {
        super(authorities);
        this.principal = principal;
        this.isAuthenticated = isAuthenticated;
        this.jwt = jwt;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public String getName() {
        return (String) this.principal;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }

    @Override
    public Object getDetails() {
        return super.getDetails();
    }

    @Override
    public void setDetails(Object details) {
        super.setDetails(details);
    }

}
