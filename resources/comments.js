console.log('com.ahungry.comments begin.')

// Net
const baseUrl = 'http://localhost:3001'

async function getData (url) {
  const response = await fetch(url, { method: 'GET' })

  return await response.json()
}

async function postData (url = '', data = {}) {
  url = baseUrl + url

  // alert(url)
  // alert(JSON.stringify(data))

  // Default options are marked with *
  const response = await fetch(url, {
    method: 'POST', // *GET, POST, PUT, DELETE, etc.
    // mode: 'no-cors', // no-cors, *cors, same-origin
    // cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    // credentials: 'same-origin', // include, *same-origin, omit
    headers: {
      'Content-Type': 'application/json'
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    // redirect: 'follow', // manual, *follow, error
    // referrer: 'no-referrer', // no-referrer, *client
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  })

  // alert(JSON.stringify(response))

  return await response.json(); // parses JSON response into native JavaScript objects
}

// Util
const ce = x => document.createElement(x)
const $ = x => document.querySelector(x)
const $$ = x => document.querySelectorAll(x)

// GUI
function labeledInput (type, lbl) {
  const label = ce('label')
  const input = ce('input')
  const span = ce('span')
  label.innerHTML = lbl
  input.type = type
  input.id = 'message'
  span.appendChild(label)
  span.appendChild(input)

  return span
}

function makeWrapper () {
  const el = ce('div')
  el.id = 'wrapper'

  return el
}

function makeForm () {
  const el = ce('form')
  el.appendChild(labeledInput('text', 'Comment: '))
  el.appendChild(ce('submit'))
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
