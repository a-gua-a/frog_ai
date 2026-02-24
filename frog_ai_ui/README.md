# frog_ai_ui

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Type Support for `.vue` Imports in TS

TypeScript cannot handle type information for `.vue` imports by default, so we replace the `tsc` CLI with `vue-tsc` for type checking. In editors, we need [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) to make the TypeScript language service aware of `.vue` types.

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

> ⚙️ **Backend API**
>
> During development the frontend proxies requests starting with `/chat` and
> `/audio` to your backend running at `http://localhost:8080`. That means the
> browser always talks to the Vite server (`localhost:5173` by default), and
> the dev server transparently forwards matching calls to the local API.
>
> ✅ **Important**: if you modify `vite.config.ts` (e.g. to add or change
> proxy rules), you must restart the dev server for the changes to take effect.
> A request to `http://localhost:5173/chat` returning 404 is a sign the proxy
> wasn't active (likely because the server was started before the config
> change). Restart with `npm run dev` and try again.
>
> The API base URL is also exposed via an env variable, but **in
> development we leave it blank** so the dev-server proxy handles requests.
> You can still set `VITE_API_BASE` for production builds if your API lives on
> a remote origin. Example (not needed during local dev):
>
> ```env
> VITE_API_BASE=https://api.example.com
> ```
> 
> If you previously added `.env.development` pointing to
> `http://localhost:8080`, you can safely remove it or leave it as a dummy
> value; it won't be used while running `npm run dev`.
> 
### Troubleshooting

- **404 from `localhost:5173/chat`**: backend not reached. Make sure the dev
  server is running and was restarted after editing proxy settings, and that
  your backend on port 8080 is up.
- **CORS errors**: if you're bypassing the proxy and calling `http://localhost:8080`
  directly, ensure the backend sends appropriate CORS headers or use the
  proxy instead.  

---

### Authentication / Login (JWT)

The front‑end now includes a simple login page (`/login`) that sends the
username/password to the backend's `/login` endpoint (GET with query
parameters by default). On successful authentication the server returns a
JWT string, which the client stores in `localStorage` and automatically
attaches to subsequent requests using the `authentication` header.

- A Pinia store (`src/stores/auth.ts`) holds the token and exposes helpers
  for checking `isAuthenticated` and performing `logout()`.
- A router guard redirects unauthenticated visitors away from `/chat` and
  forces them to log in.
- The top navigation menu shows **Login** when logged out and **Logout** when
  you are authenticated.
- Any 401 response from the API will clear the token and send you back to the
  login screen.

To try it out, start both backend and frontend, visit `/login`, and provide a
valid account. After logging in you can use the chat interface normally.



### Type-Check, Compile and Minify for Production

```sh
npm run build
```
