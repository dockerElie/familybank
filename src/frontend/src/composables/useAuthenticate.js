import axios from 'axios'
import pkceChallenge from 'pkce-challenge'

const clientId = import.meta.env.VITE_APP_FAMILYBANK_CLIENT_ID
const authorizationEndpoint = import.meta.env.VITE_APP_KEYCLOAK_AUTHORIZATION_ENDPOINT
const tokenEndpoint = import.meta.env.VITE_APP_KEYCLOAK_TOKEN_ENDPOINT
const redirectEndpoint = import.meta.env.VITE_APP_KEYCLOAK_REDIRECT_ENDPOINT
const logoutEndpoint = import.meta.env.VITE_APP_KEYCLOAK_LOGOUT_ENDPOINT
const postLogoutRedirectEndpoint = import.meta.env.VITE_APP_FB_FRONTEND_URL

export async function generatePKCETokens() {
    return pkceChallenge(128)
}

export function getAuthorizationCode(pkceChallenge) {
    const params = new URLSearchParams({
        response_type: 'code',
        client_id: clientId,
        redirect_uri: redirectEndpoint,
        scope: 'openid profile offline_access email roles microprofile-jwt',
        code_challenge: pkceChallenge,
        code_challenge_method: 'S256',
        state: 1234
    });

    return `${authorizationEndpoint}?${params.toString()}`;
}

export async function getAccessToken(code, pkceVerifier) {
    const data = new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: clientId,
        redirect_uri: redirectEndpoint,
        code_verifier: pkceVerifier,
        code,
    });

    return await axios.post(tokenEndpoint, data, {
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    })
}

export async function getRefreshToken(refreshToken, expiresIn) {
    if (Date.now() > expiresIn) {
        const data = new URLSearchParams({
            grant_type: 'refresh_token',
            client_id: clientId,
            refresh_token: refreshToken,
        });

        return await axios.post(tokenEndpoint, data, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        })
    }
}

export function oidcLogout(idToken) {
    const redirectUri = encodeURIComponent(`${postLogoutRedirectEndpoint}`);
    return `${logoutEndpoint}?id_token_hint=${idToken}&post_logout_redirect_uri=${redirectUri}`
}

