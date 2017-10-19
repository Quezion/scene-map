# threact

CLJS Library that renders 3D scenes from Clojure maps using THREE.js

**Note: Not under development,** and not in an easily runnable state. [Check the README from an earlier commit](https://github.com/Quezion/threact/tree/96c10e01e95382b7b7db2dd4c3865b2fa5ee9940) to get an idea of what this library set out to do.

This was originally a separation of code from a larger application I was working on. It was browser based renderer for the (very opaque model-files) from FFXIV. This goal was accomplished [(quick thanks to the author of FFXIV Explorer for his research)](http://ffxivexplorer.fragmenterworks.com/research.php), but the capabilities of that renderer were not fully supported within Threact's logic.

There are still some advantages to a declarative rendering approach, such as:
* The ability to stream videos in a cross-platform, remotely-rendered manner by only broadcasting changes in scene state
* Easy to use abstraction for applications where lower graphical performance is acceptable, such as visualization and certain types of games
* Possibility for the clientside renderer to apply its own "graphical filters" directly to the incoming 3D scene

The current version of `master` is after I scrapped a large portion for a cleaner rewrite (which clearly hasn't happened :). As a result, the state of the repo at [the aforelinked commit 96c10e01](https://github.com/Quezion/threact/tree/96c10e01e95382b7b7db2dd4c3865b2fa5ee9940) may be of more interest.

While this project will remain unrealized, I'm hopeful that this technique may be incidentally reincarnated by some future developer. Godspeed to them!

## License

MIT License

Copyright (c) [2017] [Quest Yarbrough]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
