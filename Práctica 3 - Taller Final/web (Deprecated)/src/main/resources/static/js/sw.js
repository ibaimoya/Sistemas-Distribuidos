/** Constante que contiene la versión del Service Worker. */
const CACHE = 'v1';

/** 
 * Se crea un array de URLs que serán cacheadas por el Service Worker.
 * Contiene rutas a páginas esenciales que deben estar disponibles de forma rápida y offline:
 *      - Página raíz/índice.
 *      - Página de inicio de sesión.
 *      - Página de registro.
 */
const urlsToCache = [
  '/',
  '/login',
  '/register'
];

/**
 * Instalación del Service Worker.
 * @description Este evento se activa cuando el Service Worker se instala por primera vez.
 * Se utiliza para abrir el caché y almacenar las URLs definidas en el array `urlsToCache`.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
self.addEventListener('install', e =>
    e.waitUntil(
      caches.open(CACHE).then(cache => cache.addAll(urlsToCache))
    )
  );
  
  self.addEventListener('fetch', e => {
    e.respondWith(
      caches.match(e.request).then(r => r || fetch(e.request))
    );
  });