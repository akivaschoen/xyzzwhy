# xyzzwhy

A Clojure-powered Twitter bot who goes on random adventures through a
dreamlike world. 

## Installation

Bring it up in the repl and repeatedly issue (create-tweet). Requires a database populated 
from xyzzwhy-bot.db (and possibly an authorized Twitter account if you want to actually post).

## Current Status

Very basic and completely random for now. Future plans (roughly in order):

- Refactor the data functions to allow xyzzwhy-bot to get its data from xyzzwhy-bot.db rather than assuming a populated database.
- Add more depth and branches. Things should be able to have potential follow-up segments.
- Maintain xyzzwhy's state: health, inventory, and so forth.
- Create consistency in the adventure.
- Procedurally-generated 'dungeons' rather than just randomized events.
- Make it funny (or at least interesting).

### Ongoing

- Add more data. Tons more data.

## License

Copyright © 2014 Akiva R. Schoen

Distributed under the MIT License (see LICENSE for details).
