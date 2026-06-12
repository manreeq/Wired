# Wired

**A Spotify-integrated music social network.** Wired turns what you're listening to into a shared, real-time social experience — see what friends are playing as it happens, post and react to tracks, and explore your listening stats. Built as a group project for CSCI 42.

## Project Status

> ⚠️ **The hosted backend is currently offline.** It has been taken down while we migrate it to a new hosting service. As a result, the live deployment is temporarily unavailable. Our goal is to bring Wired back as a fully web-accessible app once the migration is complete.

## Features

- **Spotify login** — sign in with your Spotify account via OAuth 2.0.
- **Live listening feed** — a real-time feed of what your friends are currently playing, updated automatically as their playback changes.
- **Social posts** — share songs, albums, and playlists by pasting a Spotify link; the post type is detected automatically.
- **Comments & reactions** — comment on and react to posts in the feed.
- **Friends** — send, accept, and decline friend requests, and remove friends, with safeguards against duplicate or self-requests.
- **Profiles & privacy** — view your own and others' profiles, with a privacy toggle to control who can see your activity.
- **Listening analytics** — dashboards for your top songs, top artists, top albums, and total listening time.

## Tech Stack

**Frontend**
- React 19 + Vite
- React Router for client-side routing
- STOMP.js + SockJS for real-time WebSocket updates
- CSS Modules for styling, react-hot-toast for notifications

**Backend**
- Spring Boot 3.2 (Java 17)
- Spring Data JPA for persistence
- Spring WebSocket (STOMP) for real-time messaging
- Spotify Web API integration via Retrofit / OkHttp
- JWT-based sessions (jjwt) with encrypted Spotify refresh tokens

**Database**
- PostgreSQL

## Architecture

Wired is a two-tier application: a React single-page app talks to a Spring Boot REST + WebSocket API, which persists to PostgreSQL and integrates with the Spotify Web API.

```
React SPA  ──REST + WebSocket──►  Spring Boot API  ──JPA──►  PostgreSQL
                                         │
                                         └──►  Spotify Web API
```

**Authentication.** Login is handled through Spotify OAuth 2.0. After the OAuth exchange, the backend issues its own JWT, delivered in an HttpOnly cookie, which authenticates subsequent API requests. Each user's Spotify refresh token is encrypted before being stored, and access tokens are transparently refreshed when they near expiry.

**Real-time listening feed.** A scheduled service on the backend periodically polls each user's currently-playing track from Spotify. When playback changes, it broadcasts an activity update over a STOMP topic; the frontend subscribes to that topic over a SockJS WebSocket connection and updates the feed live — no refresh required.

**Domain model.** Posts are modeled as a base post type with song, album, and playlist variants. Music metadata is normalized into songs, albums, and artists (with join relationships between them), alongside users, friend connections, comments, reactions, and listening activity.

## Roadmap

- Migrate the backend to a new hosting service and restore the live deployment.
- Make Wired fully web-accessible so anyone can sign in with Spotify and use it without running it locally.
