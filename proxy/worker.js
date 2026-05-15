/**
 * Cloudflare Worker — IPTV HLS Proxy
 * Proxies HTTP IPTV streams over HTTPS, rewrites m3u8 segment URLs.
 * Deploy: npx wrangler deploy   (or paste into CF dashboard)
 */

addEventListener('fetch', event => {
  event.respondWith(handleRequest(event.request));
});

const CORS = {
  'Access-Control-Allow-Origin':  '*',
  'Access-Control-Allow-Methods': 'GET, HEAD, OPTIONS',
  'Access-Control-Allow-Headers': '*',
};

async function handleRequest(request) {
  if (request.method === 'OPTIONS') {
    return new Response(null, { headers: CORS });
  }

  const url      = new URL(request.url);
  const targetUrl = url.searchParams.get('url');

  if (!targetUrl) {
    return new Response('Usage: ?url=http://iptv-server/stream.m3u8', {
      status: 400,
      headers: { 'Content-Type': 'text/plain', ...CORS },
    });
  }

  let upstream;
  try {
    upstream = await fetch(targetUrl, {
      headers: { 'User-Agent': 'Mozilla/5.0' },
    });
  } catch (err) {
    return new Response(`Fetch error: ${err.message}`, {
      status: 502,
      headers: { 'Content-Type': 'text/plain', ...CORS },
    });
  }

  const contentType = upstream.headers.get('content-type') || '';
  const isM3u8      = contentType.includes('mpegurl') ||
                      targetUrl.toLowerCase().includes('.m3u8');

  if (isM3u8) {
    // Rewrite every non-comment line URL to go through this proxy
    const text        = await upstream.text();
    const workerBase  = url.origin + url.pathname;

    const rewritten = text.split('\n').map(raw => {
      const line = raw.trim();
      if (!line || line.startsWith('#')) return raw;

      const absUrl = line.startsWith('http')
        ? line
        : new URL(line, targetUrl).href;

      return `${workerBase}?url=${encodeURIComponent(absUrl)}`;
    }).join('\n');

    return new Response(rewritten, {
      headers: {
        'Content-Type': 'application/vnd.apple.mpegurl',
        'Cache-Control': 'no-cache',
        ...CORS,
      },
    });
  }

  // Binary segments — stream the body straight through
  return new Response(upstream.body, {
    status: upstream.status,
    headers: {
      'Content-Type': upstream.headers.get('content-type') || 'video/mp2t',
      'Cache-Control': 'public, max-age=10',
      ...CORS,
    },
  });
}
