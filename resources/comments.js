console.log('com.ahungry.comments begin.')

// Net
// const baseUrl = 'http://localhost:3001'
const baseUrl = window.location.protocol + '//' + window.location.host
var sourceHref = window.location.href
var username
var password

async function getData (url) {
  url = url + '?href=' + sourceHref
  const response = await fetch(url, { method: 'GET' })

  return await response.json()
}

async function postData (url = '', data = {}) {
  url = baseUrl + url

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ href: sourceHref, ...data })
  })

  return await response.json();
}

// Util
const ce = x => document.createElement(x)
const $ = x => document.querySelector(x)
const $$ = x => document.querySelectorAll(x)

// GUI
function labeledThing (thing, type, lbl, id, styles = {}) {
  const label = ce('label')
  const input = ce(thing)
  const span = ce('div')
  label.innerHTML = lbl
  label.style.display = 'block'
  input.type = type
  input.name = id
  input.id = id
  Object.keys(styles).forEach(key => {
    input.style[key] = styles[key]
  })
  span.appendChild(label)
  span.appendChild(input)

  return span
}

const labeledInput = labeledThing.bind(labeledThing, 'input', 'text')
const labeledTextarea = labeledThing.bind(labeledThing, 'textarea', 'text')
const labeledPassword = labeledThing.bind(labeledThing, 'input', 'password')

function makeSubmit () {
  const el = ce('button')
  el.innerHTML = 'Submit'

  return el
}

function makeWrapper () {
  const el = ce('div')
  el.id = 'wrapper'
  el.style.border = '0px solid #000'
  el.style.margin = 'auto'
  el.style.padding = '50px'

  return el
}

function makeFeedback () {
  const el = ce('div')
  el.id = 'feedback'
  el.style.color = 'red'

  return el
}

var gui = {
  feedback: makeFeedback(),
  form: undefined,
  wrapper: makeWrapper(),

  // Form fields
  username: labeledInput('Username: ', 'username'),
  password1: labeledPassword('Password: ', 'password1'),
  password2: labeledPassword('Confirm Password (optional): ', 'password2'),
  comment: labeledTextarea('Comment (supports markdown): ', 'message', { width: '300px', 'height': '100px' }),
  submit: makeSubmit(),
}

function makeForm (inputs, cb) {
  const el = ce('form')

  el.innerHTML = `<h3>Sign in or comment:</h3>
<p style='font-size:.8rem;'>
If you've never commented before, a new account will be created for you.
Don't forget your password.
</p>
`

  inputs.forEach(input => {
    el.appendChild(gui[input])
  })
  el.onsubmit = cb

  return el
}

function doLogin (m) {
  username = m.username
  password = m.password1
  gui.wrapper.removeChild(gui.form)
  gui.wrapper.appendChild(makeFormLoggedIn())
}

function makeFormLoggedOut () {
  return makeForm(
    ['username', 'password1', 'password2', 'submit'],
    async (e) => {
      e.preventDefault()
      setTimeout(async () => {
        try {
          const username = $('#username').value
          const password1 = $('#password1').value
          const password2 = $('#password2').value
          const res = await postData('/login', { username, password1, password2 })

          if (res && res.error) {
            $('#feedback').innerHTML = res.error
          }

          if (res.username) {
            $('#feedback').innerHTML = 'Logged in!'
            doLogin(res)
          }
        } catch (reason) {
          $('#feedback').innerHTML = JSON.stringify(reason)
          console.error(reason)
        }
      }, 10)
    })
}

function makeFormLoggedIn () {
  return makeForm(
    ['comment', 'submit'],
    async (e) => {
      e.preventDefault()
      setTimeout(async () => {
        try {
          const message = $('#message').value
          const res = await postData('/comment', { username, password, message })
          console.log('Post send back: ', res)

          if (res && res.error) {
            $('#feedback').innerHTML = res.error
          } else {
            renderComments(res)
            $('#message').value = ''
          }
        } catch (reason) {
          console.log(reason)
        }
      }, 10)
    })
}

function renderComment ({ date, message, username }) {
  const el = ce('div')
  el.style.background = 'rgba(0, 0, 0, .1)'
  el.style.margin = '5px'
  el.style.padding = '15px'
  el.innerHTML = `
<span style='font-size:.8rem;'>
  posted by <b>${username}</b> on ${date}:
</span><br>
${message}`

  return el
}

function makeCommentsContainer () {
  let el = $('#comments')

  if (!el) {
    el = ce('div')
    el.id = 'comments'
    gui.wrapper.appendChild(el)
  }

  el.innerHTML = `
Comments for <b>${sourceHref}</b>
<br><span style="font-size:.7em">hosted by
<a href="http://comments.ahungry.com" target="_blank">http://comments.ahungry.com</a></span>
<br><span style="font-size:.7em">AGPLv3 source
<a href="https://github.com/ahungry/com.ahungry.comments" target="_blank">https://github.com/ahungry/com.ahungry.comments</a></span>
`

  return el
}

function renderComments (comments) {
  const elC = makeCommentsContainer()

  comments.map(renderComment).map(el => elC.appendChild(el))
  requestResize()

  return elC
}

// Main things
async function doComments () {
  const comments = await getData('/comments')

  return renderComments(comments)
}

function requestResize () {
  if (!window || !window.parent) return

  window.parent.postMessage({
    type: 'resize',
    w: gui.wrapper.scrollWidth,
    h: gui.wrapper.scrollHeight + 200,
  }, '*')
}

async function init () {
  const comments = await doComments()
  gui.wrapper.appendChild(comments)
  gui.form = makeFormLoggedOut()
  gui.wrapper.appendChild(gui.form)
  gui.wrapper.appendChild(gui.feedback)
  document.body.appendChild(gui.wrapper)

  requestResize()
}

window.addEventListener('message', receiveMessage, false);

var wasGetHrefDone = false

function handler (m) {
  switch (m.type) {
    case 'getHref':
      sourceHref = m.val
      wasGetHrefDone = true
      break
  }
}
function receiveMessage (event) {
  // Events that get handled up at the potential iframe source.
  const { data } = event
  handler(data)
  // if (event.origin === 'http://comments.ahungry.com' ||
  //   event.origin === 'http://localhost:3001') {
  //   const { data } = event
  //   handler(data)
  // }
}

function didSetup () {
  return wasGetHrefDone
}

function slowInit () {
  // Incase they are iframing it
  if (window && window.parent && window.parent.postMessage) {
    window.parent.postMessage({
      type: 'getHref',
    }, '*')
  }

  if (!didSetup()) {
    return setTimeout(() => {
      slowInit()
    }, 10)
  }

  init()
}
// Stagger loading until we get the proper source href
if (window.parent && window.parent != window) {
  slowInit()
} else {
  init()
}

console.log('com.ahungry.comments fin.')
