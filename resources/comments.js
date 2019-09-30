console.log('com.ahungry.comments begin.')

// Net
const baseUrl = 'http://localhost:3001'

async function getData (url) {
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
    body: JSON.stringify(data)
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

  return el
}

function makeForm () {
  const el = ce('form')
  el.appendChild(labeledInput('Username: ', 'username'))
  el.appendChild(labeledPassword('Password: ', 'password'))
  el.appendChild(labeledTextarea('Comment: ', 'message', { width: '300px', 'height': '100px' }))
  el.appendChild(ce('submit'))
  el.appendChild(makeSubmit())
  el.onsubmit = async (e) => {
    e.preventDefault()
    setTimeout(async () => {
      try {
        const message = $('#message').value
        const res = await postData('/comment', { username: 'Test', password: 'Test', message })
        console.log('Post send back: ', res)
        $('#message').value = ''
        renderComments(res)
      } catch (reason) {
        console.log(reason)
      }
    }, 10)

    return false
  }

  return el
}

var elCommentsContainer

function renderComment ({ message }) {
  const el = ce('div')
  el.innerHTML = message

  console.log(message)

  return el
}

function makeCommentsContainer () {
  const el = $('#comments')

  if (el) {
    el.parentNode.removeChild(el)
  }

  elCommentsContainer = ce('div')
  elCommentsContainer.id = 'comments'
  gui.wrapper.appendChild(elCommentsContainer)

  return elCommentsContainer
}

function renderComments (comments) {
  const elC = makeCommentsContainer()

  comments.map(renderComment).map(el => elC.appendChild(el))

  return elC
}

// Main things
const gui = {
  form: makeForm(),
  wrapper: makeWrapper(),
}

async function doComments () {
  const comments = await getData('/comments')

  return renderComments(comments)
}

async function init () {
  gui.wrapper.appendChild(gui.form)
  const comments = await doComments()
  gui.wrapper.appendChild(comments)
  document.body.appendChild(gui.wrapper)
}

init()
console.log('com.ahungry.comments fin.')
