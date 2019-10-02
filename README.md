# com.ahungry.comments

Comment system.

Check out https://comments.ahungry.com to see it in action.

Sample from site:
```
posted by ahungry on 2019-10-01T01:00:10.620:
Check out the links for more information!

posted by ahungry on 2019-10-02T01:27:01.246:
This is a comment system that you can include with a single line of javascript:

<script src="https://comments.ahungry.com/comments.js"></script>
It supports full markdown in the comment blocks, and should safely escape some HTML.

posted by ahungry on 2019-10-02T01:32:15.082:
           <iframe frameborder="0"
             height="500"
             width="800"
             id="comments-frame"
             style="width:800px;height:auto;"
             src="https://comments.ahungry.com/?w=500"></iframe>
           <script src="https://comments.ahungry.com/iframe.js"></script>
Using the above snippet, you can also include the comment area as an iframe, to avoid impacting your main site with javascript injection.

posted by ahungry on 2019-10-02T01:34:18.225:
Oh, for now, it requires no registration or setup to use on your own site, but please understand it's essentially ephemeral at that point!

If you want to ensure your comments for many different sites do not disappear, you should clone the AGPLv3 codebase and run your own copy (and then add the single include on your sites).
```

# Why didn't you add 'XYZ Auth provider' like other 'ABC Service' has?

I don't think many of the common IDP providers (Google SAML / IDP,
Facebook etc.) are very privacy friendly (one of the main reasons I
wrote this implementation was that my old one was very heavy on
ads/tracking...hundreds of remote assets being downloaded just to
include their javascript for commenting on my site).

# License

Copyright © 2019 Matthew Carter <m@ahungry.com>

AGPLv3
