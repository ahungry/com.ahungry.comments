// Util
const ce = x => document.createElement(x)
const $ = x => document.querySelector(x)
const $$ = x => document.querySelectorAll(x)

window.addEventListener('message', receiveMessage, false);

function handler (m) {
  console.log('handler', { m })

  switch (m.type) {
    case 'getHref':
      $('#comments-frame').contentWindow.postMessage(
        {
          type: 'getHref',
          val: window.location.href,
        }, '*')
      break

    case 'resize':
      const { w, h } = m
      $('#comments-frame').width = w
      $('#comments-frame').height = h
      $('#comments-frame').style.width = `${w}px`
      $('#comments-frame').style.height = `${h}px`
      $('#comments-frame').style.display = `block`
      break
  }
}

function receiveMessage (event) {
  // Events that get handled up at the potential iframe source.
  if (event.origin === 'http://comments.ahungry.com' ||
    event.origin === 'https://comments.ahungry.com' ||
    event.origin === 'http://localhost:3001') {
    const { data } = event
    handler(data)
  }
}
