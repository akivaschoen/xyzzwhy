 (ns xyzzwhy-bot.db
  (:refer-clojure :exclude [remove sort find])
  (:use [monger.query])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri]]
            [monger.collection :refer [insert-batch remove]]))

(def event-types
  [{:text :location-event}
   {:text :action-event}])

(def location-events
  [{:text "You have entered {:class :location :config [:no-prep]}."}
   {:text "You are {:class :location}."}
   {:text "You are {:class :location}."}
   {:text "The drugs are wearing off. You are {:class :location}."}
   {:text "The spell effects are wearing off. You are {:class :location}."}
   {:text "You are standing {:class :direction} of {:class :location :config [:no-prep]}."}
   {:text "You stumble into {:class :location :config [:no-prep]}."}
   {:text "You come across {:class :location :config [:no-prep]}."}
   {:text "You wake up from an odd dream. You are {:class :location}."}
   {:text "You open the secret door only to see {:class :location :config [:no-prep]}."}
   {:text "You find yourself {:class :location}."}
   {:text "You start doing the worm until you find yourself {:class :location}."}
   {:text "You wake up {:class :location}."}
   {:text "You climb down the tree and find yourself {:class :location}."}
   {:text "The taxi driver randomly drops you off {:class :location}."}
   {:text "The fog clears and you find yourself {:class :location}."}
   {:text "You jump out of a moving car, roll down a hill, and find yourself {:class :location}."}
   {:text "After walking for a long time, you find yourself {:class :location}."}
   {:text "You find your way blindly and end up {:class :location}."}
   {:text "No matter how hard you try, you still end up {:class :location}."}
   {:text "You climb out of the treasure chest. You are now {:class :location}."}
   {:text "You come to {:class :location}."}
   {:text "You follow a winding path only to find yourself {:class :location}."}
   {:text "The elevator doors open to reveal {:class :location :config [:no-prep]}."}
   {:text "The trapdoor drops open beneath you and you land {:class :location}."}
   {:text "You get tangled up in a revolving door. You stumble out into {:class :location :config [:no-prep]}."}
   {:text "After scrambling through some dense underbrush, you find yourself {:class :location :config [:no-prep]}."}
   {:text "You squeeze out of the sewage outflow and tumble into {:class :location [:no-prep]}."}
   {:text "The tornado deposits you {:class :location}."}
   {:text "After being shot out of a cannon, you land {:class :location}."}
   {:text "Hands on your hips, you survey {:class :location :config [:no-prep]} {:class :adverb}."}
   {:text "You have reached a dead-end. You start moonwalking away."}])

(def action-events
  [{:text "You awake from a nightmare. You saw yourself {:class :location}. The corpse of {:class :person} was there, holding {:class :item}."}
   {:text "You grab {:class :item}, hoping {:class :person} doesn't notice."}
   {:text "The radio crackles to life. 'Mayday, mayday, it's {:class :person} calling. We're in trouble. We need assistance. Mayday, mayday.'"}
   {:text "{:class :actor} drops {:class :item}, looks at you {:class :adverb}, then leaves."}
   {:text "{:class :actor} gently places {:class :item} and backs away slowly."}
   {:text "Suddenly, {:class :actor} {:class :action} you."}
   {:text "{:class :actor} {:class :action} {:class :actor}."}
   {:text "{:class :actor} {:class :action} you."}
   {:text "{:class :actor} drops {:class :item} here."}
   {:text "{:class :person} marches up to you and says, 'Hello please.'"}
   {:text "{:class :person} starts breakdancing and won't stop no matter how much you scream."}
   {:text "{:class :actor} attacks you and knocks you out! You awake sometime later {:class :location}."}
   {:text "{:class :person} appears in a puff of smoke and shouts, 'You will never see your {:class :item :config [:no-prep]} again!'"}
   {:text "You startle {:class :person} who drops {:class :item} and runs away."}
   {:text "{:class :person} slams down a half-empty glass of bourbon. 'All this nonsense about {:class :item} needs to stop! I can't take it anymore!'"}
   {:text "{:class :person} suddenly shrieks."}
   {:text "You get tired of waiting for your Uber and decide to walk to {:class :location :config [:no-prep]} instead."}
   {:text "The phone rings. {:class :person} stares at it {:class :adverb}. You refuse to answer it. Eventually the ringing stops."}
   {:text "You start eating {:class :food} and don't stop until you're done."}
   {:text "You eat {:class :food}."}
   {:text "You eat {:class :food}. {:class :actor} looks on {:class :adverb}."}
   {:text "You feel a little famished so you eat {:class :food}."}
   {:text "You take a sip of {:class :drink}."}
   {:text "You check your inventory. You are empty-handed."}
   {:text "You check your inventory. You are carrying {:class :item}, {:class :item}, and {:class :item}."}
   {:text "You check your inventory. You have {:class :item} and {:class :item}."}
   {:text "You open up {:class :book}. Someone has scribbled all over the margins. You throw it down on the floor in disgust."}
   {:text "You open up {:class :book}. Someone has left a recipe for beef stew inside."}
   {:text "You open up {:class :book}. You read a bit before tossing it over your shoulder and then doing the electric slide."}
   {:text "{:class :actor} suddenly appears out of the shadows, hisses at you, then scrambles away like a spider."}
   {:text "{:class :actor} picks up {:class :item}."}
   {:text "An overhead loudspeaker crackles to life, 'Citizen! Report immediately to the nearest self-incrimination booth.'"}
   {:text "You start spinning around and around while {:class :person} claps and cheers."}
   {:text "{:class :person} is calling from {:class location :config [:no-prep]} asking for {:class :item}."}
   {:text "You peek out the window. {:class :person} is messing around with your mailbox. You crouch in fear."}
   {:text "In the distance, you hear {:class :person} let the bass drop."}
   {:text "With a wide grin, @clive logs into Admiral Krag."}
   {:text "You check your health: you are {:class :diagnose}."}])

(def secondary-events 
  [{:text "You see {:class :item} here."}
   {:text "You see {:class :item} here. It looks oddly familiar."}
   {:text "There is {:class :item} here."}
   {:text "You pick up {:class :item}. Was this here before?"}
   {:text "You pick up {:class :item}."}
   {:text "You drop {:class :item}."}
   {:text "You find {:class :item} here but decide to leave it alone."}
   {:text "{:class :actor} is here."}
   {:text "{:class :actor} is here{:class :actor-action}"}
   {:text "You find {:class :actor}{:class :actor-action}"}
   {:text "{:class :person} {:class :dialogue}"}
   {:text "{:class :person} {:class :dialogue}"}
   {:text "{:class :person} {:class :dialogue}"}
   {:text "{:class :person} {:class :dialogue}"}
   {:text "{:class :actor} is here searching for {:class :item}."}
   {:text "{:class :actor} is here hoping to run into {:class :actor}."}
   {:text "{:class :actor} follows you."}
   {:text "A hollow voice intones, '{:class :intonation}'"}
   {:text "A hollow voice intones, '{:class :intonation}'"}
   {:text "Something smells {:class :scent} here."}
   {:text "You hear {:class :noise} in the distance."}
   {:text "You hear the sound of {:class :noise} nearby."}
   {:text "The wind howls in the distance."}
   {:text "It appears abandoned."}
   {:text "Someone has been here recently."}
   {:text "There are fresh footprints here."}
   {:text "It seems that no one has been here for a long time."}
   {:text "Someone has attached marionnette wires to your hands, feet, and head."}
   {:text "Someone has left a running bulldozer here."}
   {:text "The words 'eat dulp' are spray-painted on the wall here.'"}
   {:text "There has been significant damage from {:class :disaster}."}
   {:text "You see a sign here. On it is written '{:class :sign}'"}])

(def tertiary-events 
  [{:text "You aren't wearing any clothes."}
   {:text "Your shoes are on the wrong feet."}
   {:text "Your tie feels uneven."}
   {:text "You're not wearing any underwear."}
   {:text "Someone is struggling with warped Tupperware nearby."}
   {:text "You hear a box fart. Someone is playing {:class :game :type :table-top} without you."}
   {:text "You do a little jig and then whistle."}
   {:text "You clap once."}
   {:text "You have socks on your hands."}
   {:text "You feel nervous."}
   {:text "You feel anxious."}
   {:text "You feel cold."}
   {:text "You feel warm."}
   {:text "You blink really slowly."}
   {:text "You find yourself humming the theme to Too Many Cooks."}
   {:text "You hear gunfire in the distance."}
   {:text "You hear a party in the distance."}
   {:text "Someone is having fun against their will nearby."}
   {:text "You yawn."}
   {:text "You chuckle to yourself."}
   {:text "You practice frowning for awhile."}
   {:text "You begin to smile uncontrollably."}
   {:text "You wish you had your grandpappy's harmonica."}
   {:text "You are starting to feel sleepy."}
   {:text "You think about brushing your hair but change your mind."}
   {:text "You spend a few moments thinking fondly about your teeth."}
   {:text "You have rope burns got on your wrists... but from where"}
   {:text "You feel as if you're being followed."}
   {:text "A warm breeze blows by."}
   {:text "A cool breeze blows by."}
   {:text "It starts to rain."}
   {:text "A basketball bounces by."}
   {:text "You spot a balloon stuck in a tree."}
   {:text "Somehow, you've lost your {:class :garment :config [:no-article]}."}
   {:text "You hear someone nearby typing away on a manual typewriter."}
   {:text "You are starting to feel hungry."}])

(def actor-actions
  [{:text " looking {:class :adjective}."}
   {:text " dancing furiously."}
   {:text " shouting at an imaginary helicopter."}
   {:text " doing the Kenosha Kid."}
   {:text " thinking {:class :adverb} about {:class :actor}."}
   {:text " being chased around by a bee."}
   {:text " defiantly eating Scrabble tiles, one by one."}
   {:text " organizing matches."}
   {:text " juggling some balls."}
   {:text " dancing in a little circle."}
   {:text " stooping up and down like a rapper in concert."}
   {:text " drooling uncontrollably."}
   {:text ", hiding under a table."}
   {:text ", hiding under a sofa."}
   {:text ", hiding in the bushes."}
   {:text ", munching on {:class :food}."}
   {:text ", pretending to be invisible."}
   {:text ", having a coughing fit."}
   {:text ", having a sneezing fit."}
   {:text ", being menaced by {:class :animal}."}
   {:text ", ready to start some shit."}
   {:text ", examining {:class :item} with great confusion."}])

(def locations
  [{:text "tire fire" 
    :type :exterior
    :article "a" 
    :preps ["at" "near" "behind" "in front of"]
    :descriptions ["It is warm and welcoming."
                   "Someone had been roasting marshmallows here."
                   "The air here is black with despair and entropy."
                   "The sky is darkened by the hellish smoke of the endless burn."
                   "These tires are no longer the things on your car that make contact with the road."]}
   
   {:text "dildo bonfire" 
    :type :exterior
    :article "a" 
    :preps ["at" "near" "behind" "in front of"]
    :descriptions ["You look closely but don't recognize any of them."
                   "The plastic hisses and creaks in the blaze."
                   "Someone has piled up a collection of pleasuring devices, now ablaze."
                   "Surely there had to hae been a better way to punish these plastic torpedos."
                   "The air is dense with the echoes of unreached orgasms and epic frustrations."]}

   {:text "maze of twisty passages, all alike"
    :type :interior
    :article "a"
    :preps ["in"]}

   {:text "Burning Man"
    :type :exterior
    :preps ["at"]
    :descriptions ["Oddly, no one appears to be here."
                   "A tumbleweed made out of human hair stumbles by."
                   "A dust storm is approaching."
                   "It looks like it might rain soon."
                   "Clearly the drugs have begun to take hold."]}

   {:text "Shrim Healing Center"
    :type :exterior
    :article "a"
    :preps ["in" "at" "in front of" "behind"]
    :descriptions ["In the store window is an array of old television sets, all blackly inert."
                   "Someone has spray-painted 'I crave brown baths' here."
                   "From somewhere within the building, you hear the sound of repulsed joy."
                   "The building looks like it has been condemned. The door is boarded up."]}

   {:text "quicksand"
    :type :exterior
    :article "some"
    :preps ["in" "near"]
    :descriptions ["Briefly, you see a fin rise up and cruise back and forth."
                   "The surface of the quicksand gently sways, beckoning you..."
                   "Oddly, this quicksand smells like freshly cooked oatmeal."]}

   {:text "swimming pool" 
    :type :exterior
    :article "a"
    :preps ["in" "at" "near"]
    :descriptions ["The surface of the pool is almost entirely still. You are afraid to disturb it."
                   "The water has turned slightly murky; it does not look inviting."
                   "The surface of the pool is littered with leaves."
                   "An abandoned plastic float with a dinosaur's head floats lonely nearby."]}

   {:text "sauna" 
    :type :interior
    :article "a" 
    :preps ["in" "near"]
    :descriptions ["The wood paneling sweats sweetly in the oppressive heat."
                   "Great thunderheads of steam rise up from the rock basin, making it hard to see."
                   "The room is cold and dark. No one has used this sauna in years."]} 
   
   {:text "New York Public Library" 
    :type :exterior
    :article "the"
    :preps ["at" "near" "behind" "in front of"]}

   {:text "ravine" 
    :type :exterior
    :article "a" 
    :preps ["in"]
    :descriptions ["It stretches out in front of you, meandering as if drunk."
                   "It has been nearly choked to death by an avalanche at the north end."
                   "The walls of the ravine are treacherous. A trickle of water flows fitfully below."]}

   {:text "ditch" 
    :type :exterior
    :article "a" 
    :preps ["in"]
    :desriptions ["The dusty stench of aged sewage rises up like a stomach-crushing wraith."
                  "It is completely blocked here by a giant boulder. But how did it get here?"
                  "A trickle of clear water runs down the middle of it."]}

   {:text "dump" 
    :type :exterior
    :article "the"
    :preps ["at" "near" "behind" "in front of"]
    :descriptions ["In the distance, you see women searching through spires of rubbish for treasure."
                   "The hill of trash shifts dangerously beneath your feet."
                   "The mounds of garbage stretch off into the distant, murky haze."]}

   {:text "dump truck" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "behind" "in front of" "underneath"]
    :descriptions ["It's covered with a patina of black filth and oily washes of grime."
                   "Fresh off the line, this dump truck is gleaming with clean red paint."
                   "The engine is rumbling roughly to itself. Both of the doors are locked."]}

   {:text "Starbucks" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "behind" "in front of"]
    :descriptions ["It is packed tightly with hipsters."
                   "There is a surprising lack of hipsters here."
                   "It reeks of slightly burnt coffee here."]}

   {:text "park restroom stall" 
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["The door has been torn off its hinges and the walls are covered with violent scratches."
                   "Unfortunately, the toilet recently vomited up at least five gallons of excrement and dreams."
                   "You feel a sense of deja vu."
                   "You whisper to yourself, 'Home again, home again, jiggity jig.'"
                   "A lingering scents of lemon and Lysol haunt the air here."
                   "Someone has scratched your name and phone number above the toilet paper dispenser."]}

   {:text "all-you-can-eat buffet" 
    :type :interior
    :article "an"
    :preps ["at"]
    :descriptions ["It is a grid of delicious choices, all unhealthy, all alluring."
                   "Steam crowds the air."
                   "There is a dizzying array of gluten-free, vegan choices. You leave immediately."
                   "It looks abandoned."
                   "It smells of freedom and gluttony."
                   "All of the food has been replaced with wax replicas."
                   "It's in complete disarray and hasn't been tended for some time."]}

   {:text "grotto" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "behind" "in front of"]
    :descriptions ["The ceiling is sparkling with light reflected from the blue-green pool below."
                   "The water is darkened with greenish-gray algae. There's a foul odor here."
                   "The pool of water seems unusually deep. A lean, black fish swims in a circle."]}

   {:text "bedroom" 
    :type :interior
    :article "your"
    :preps ["in"]
    :descriptions ["It hasn't been cleaned in a long time. There's a pleasantly disgusting smell here."
                   "It's small and lightly furnished. The bed is unmade. Has someone been sleeping here?"
                   "There is nothing special about it."
                   "You notice an unusual stain in the carpet."
                   "You notice an unusual stain in the carpet next to a usual stain. So it goes."
                   "It's a typical bedroom. There's a pile of laundry in one corner and a computer desk in the other."] }

   {:text "McDonald's" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "White Castle" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "Taco Bell" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "dark area" 
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["It is pitch black here. You're likely to be eaten by {:class :actor}"]}

   {:text "breezy cave" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "in front of"]
    :descriptions ["There's a constant breeze rising up from the depths."
                  "Wide and low, the cave gently slopes {:class :direction}-{:class :direction} here."
                  "Here it winds up precariously. The cave seems to be breathing rapidly."]}

   {:text "forest" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "in front of"]
    :descriptions ["It is a dense, dark, and tangled choke of gnarled trees, thorny underbrush, and spiky thickets."
                   "Shot through with shafts of light, the forest before you looks serene."
                   "The trees, mostly oak and spruce, sway gently in the occasional breeze."
                   "Birds are chirping and rodents scamper through the underbrush."]}

   {:text "riverbed" 
    :type :exterior
    :article "a"
    :preps ["in" "near"]
    :descriptions ["The shallow expanse is dry as a bone and littered with rocks and branches."
                   "Here is a shallow channel through which brackish water flows fitfully."
                   "It's mostly dry, the flow of the water blocked by a beaver dams upstream."]}
   
   {:text "AT&T Store" 
    :type :exterior
    :article "an"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "Apple Store" 
    :type :exterior
    :article "an"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "ballpit" 
    :type :interior
    :article "a"
    :preps ["in" "near"]
    :descriptions ["Oddly, all of the balls here are the same color: orange."
                   "The ballpit seems unusually deep. You can't feel the bottom."
                   "You aren't certain but all clues point to there being someone or something in the ballpit."]}

   {:text "airplane" 
    :type :interior
    :article "an"
    :preps ["in"]
    :descriptions ["There's no one else on board."
                   "You hear strange noises coming from the restroom."
                   "Somehow you have a dozen packets of pretzels."
                   "Someone drank your Fresca while you were napping."
                   "It's pitch black outside. Can grues fly?"
                   "The pilot says, 'We've reached our cruising altitude of 30 feet.'"
                   "The plane has been going straight up for hours now."]}

   {:text "trunk of a car" 
    :type :interior
    :article "the"
    :preps ["in"]
    :descriptions ["It is well upholstered."
                   "A tire iron is digging into your back a little bit."
                   "There's a half-eaten bag of Bugles here."
                   "With all the trash in here, there's barely any room for you."
                   "It's pitch black. Not enough room for a grue in here, at least."]}

   {:text "coffin" 
    :type :interior
    :article "a"
    :preps ["in" "near" "in front of"]
    :descriptions ["It is well upholstered."
                   "It smells of cotton candy in here for some reason."
                   "It smells of Aquanet in here. Makes sense."
                   "It's pitch black. It probably doesn't matter if there are grues or not."]}

   {:text "hugbox"
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["You feel at home again."
                   "It's very warm in here. Perhaps... too warm."
                   "It smells of stale urine and lies, lies, lies..."]}

   {:text "haunted house" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]
    :descriptions ["At the top of the hill, the house shrugs under its own entropy."
                   "An orange light wanders from window to window."
                   "The antebellum abode, white in its gaudy shame of elegance, has been overgrown by kudzu and rotting vines."]}

   {:text "graveyard" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]
    :descriptions ["There is a freshly laid grave nearby."
                   "There is an open grave nearby. It's empty." 
                   "There is an open grave nearby. There's a phone book in it." 
                   "There is an open grave nearby. It's full of {:class :drink}."
                   "There are fresh footprints here."
                   "A lazy mist wanders aimlessly amongst the shifted tombstones. A cold light spills down from behind a tree."
                   "Long ago, the upright tombstones had been replaced by durable plastic bricks to minimize upkeep."
                   "You see a mausoleum here covered with dark green moss. It looks vaguely familiar."]}

   {:text "playground" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "behind" "in front of"]
    :descriptions ["Freshly built, it looks like it has never been used. You see not a scratch or a ding on any of the equipment."
                   "Most of the equipment is missing or broken. In the distance, swings squeak loneliness in the slight breeze."
                   "A picnic table is nearby, burdened by a fresh birthday party except no one is around. Someone is turning 6 today... but who?"]}

   {:text "pile of diapers" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "behind" "in front of" "underneath"]}

   {:text "meeting" 
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["The room is comically overwhelmed by tripod stands crowded with colorful charts."
                   "The room is empty. The projector is on, showing random photos of cats at play."
                   "The table is covered, end-to-end, by neat stacks of donuts of various heights."
                   "The chairs are all occupied by cobweb-encrusted skeletons."
                   "The room is almost full of balloons."]}

   {:text "Luby's" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}])

(def dialogues
  [{:text "asks, 'Have you ever seen an elephant throw up?'"}
   {:text "asks, 'Why am I holding this pitchfork?'"}
   {:text "asks, 'How long is a man?'"}
   {:text "asks, 'Where have you been?'"}
   {:text "asks, 'Would you like to see my collection of tiny ceiling fans?'"}
   {:text "asks, 'Which one are you?'"}
   {:text "asks, 'Can I have a hug?'"}
   {:text "asks, 'Are you following me?'"}
   {:text "asks, 'Does it smell like {:class :food} in here to you?'"}
   {:text "chants, 'It's time to pay the price.'"} 
   {:text "mumbles, 'You can't go up against city hall.'"}
   {:text "mumbles, 'One day I'm going to burn this place to the ground.'"}
   {:text "mumbles, 'Skrillex ruined it all for everybody.'"}
   {:text "mumbles, 'I've never been to Beliza.'"}
   {:text "says, 'I've been waiting for you.'"}
   {:text "says, 'I can't find my heirloom clown suit."}
   {:text "says, 'I can't find my {:class :garment :config [:no-article]}.'"}
   {:text "says, 'No money? No hamburger!"}
   {:text "says, 'It's like drinking a meatloaf!'"}
   {:text "says, 'Took you long enough.'"}
   {:text "says, 'I'm a brown-belt in speed tai chi."}
   {:text "says, 'I'm stuck in a poo loop.'"}
   {:text "says, 'If you asked me to have sex with you, I wouldn't say \"no\"."}
   {:text "shouts, 'You can't go up against city hall!'"}
   {:text "shouts, 'You can't fold a cat!'"}
   {:text "shouts, 'They're having a brownout in Lagos!'"}
   {:text "shouts, 'Don Quixote! Swingin' from a pipe!'"}
   {:text "shrieks, 'What's this shit I keep hearing about erections?!'"}
   {:text "shrieks, 'I'm living on the edge!'"}
   {:text "shrieks, 'Boiled soil!'"}
   {:text "snarls, 'Siddown before ya fall down!'"}
   {:text "whispers, 'I've always wanted to be a creepy uncle.'"}
   {:text "whispers, 'Fee was a Buddhist prodigy.'"}
   {:text "whispers, 'There squats the brown clown.'"}])

(def intonations
  [{:text "Toast goes in the toaster."}
   {:text "For those who can make the journey, there is a place."}
   {:text "Plugh."}
   {:text "Your pilikia is all pau."}
   {:text "The owls are not what they seem."}
   {:text "Puch."}
   {:text "Guch."}
   {:text "Porluch."}
   {:text "Spigot."}
   {:text "Bloyoy."}
   {:text "Sorry but it couldn't be helped."}
   {:text "Clean up in aisle 8A."}
   {:text "Rabbit feces."}
   {:text "Consider deeply the baked ham."}
   {:text "You can't go up against city hall."}])

(def signs
  [{:text "Burma shave!"}
   {:text "It's time to pay the price."}
   {:text "You can't go up against city hall."}
   {:text "For those who can make the journey, there is a place."}
   {:text "Here lies Hammerdog, a dog made of hammers."}
   {:text "Here lies Knifekitten, a kitten made of knives."}
   {:text "When you're not reading this, it's written in Spanish."}
   {:text "Now you know how hard it is to say 'Irish wristwatch'."}])

(def books
  [{:text "the Bible"
    :article "a"
    :preps ["copy of"]}

   {:text "Catcher in the Rye"
    :article "a"
    :preps ["copy of"]}

   {:text "Infinite Jest"
    :article "a"
    :preps ["copy of"]}

   {:text "Gravity's Rainbow"
    :article "a"
    :preps ["copy of"]}

   {:text "A Prayer for Owen Meany"
    :article "a"
    :preps ["copy of"]}

   {:text "Hitchhiker's Guide to the Galaxy"
    :article "a"
    :preps ["copy of"]}])

(def directions
  [{:text "north"}
   {:text "northeast"}
   {:text "east"}
   {:text "southeast"}
   {:text "south"}
   {:text "southwest"}
   {:text "west"}
   {:text "northwest"}])

(def persons
  [{:text "Samuel L. Jackson"
    :gender :male}

   {:text "Chris Makepeace"
    :gender :male}

   {:text "Frances McDormand"
    :gender :female}

   {:text "Whoopi Goldberg"
    :gender :female}

   {:text "Katy Perry"
    :gender :female}

   {:text "Lena Horne"
    :gender :female}

   {:text "Justin Bieber"
    :gender :male}

   {:text "Neil deGrasse Tyson"
    :gender :male}

   {:text "Tim Heidecker"
    :gender :male}

   {:text "Eric Wareheim"
    :gender :male}

   {:text "Jim J. Bullock"
    :gender :male}

   {:text "Johnny Cash"
    :gender :male}

   {:text "a police officer"}

   {:text "Alex Trebek"
    :gender :male}

   {:text "Craig Ferguson"
    :gender :male}

   {:text "Geoff Petersen"
    :gender :male}

   {:text "Stephen King"
    :gender :male}

   {:text "Catmeat Clive"
    :gender :male}

   {:text "Jorts Morgan"
    :gender :male}

   {:text "Construction Charles"
    :gender :male}

   {:text "Nancy Grace"
    :gender :female}

   {:text "Lindsay Lohan"
    :gender :female}

   {:text "Barack Obama"
    :gender :male}

   {:text "Abe Vigoda"
    :gender :male}

   {:text "Louis Gray"
    :gender :male}
   
   {:text "Russell Brand"
    :gender :male}
   
   {:text "Gene Shalit"
    :gender :male}

   {:text "Brad Pitt"
    :gender :male}

   {:text "Bill Maher"
    :gender :male}

   {:text "Grace Jones"
    :gender :female}

   {:text "George W. Bush"
    :gender :male}

   {:text "your mom"}

   {:text "a bunch of kids"}

   {:text "a crowd of Yoga enthusiasts"}

   {:text "George Clooney"
    :gender :male}

   {:text "James Franco"
    :gender :male}

   {:text "Jonah Hill"
    :gender :male}

   {:text "Scarlet Johansson"
    :gender :female}

   {:text "a gas station attendant"}

   {:text "Lena Dunham"
    :gender :female}

   {:text "Hillary Clinton"
    :gender :female}

   {:text "Craig T. Nelson"
    :gender :male}

   {:text "Thomas Pynchon"
    :gender :male}

   {:text "@akiva"
    :gender :male}

   {:text "@veo_"
    :gender :male}

   {:text "@vmcny"
    :gender :male}

   {:text "@wolfpupy"
    :gender :female}

   {:text "@KamenPrime"
    :gender :male}

   {:text "@neonbubble"
    :gender :male}

   {:text "@micahwittman"
    :gender :male}

   {:text "@itafroma"
    :gender :male}

   {:text "@clive"
    :gender :male}

   {:text "@mokargas"
    :gender :male}

   {:text "@feelingmean"
    :gender :male}

   {:text "Zombie Carl Sagan"
    :gender :male}])

(def actions
  [{:text "attacks"}
   {:text "ignores"}
   {:text "tickles"}
   {:text "stands uncomfortably close to"}
   {:text "pets"}
   {:text "examines"}
   {:text "flirts with"}])

(def adjectives
  [{:text "worried"}
   {:text "relieved"}
   {:text "aroused"}
   {:text "afraid"}
   {:text "nonplussed"}
   {:text "sleepy"}
   {:text "hungry"}
   {:text "thirsty"}
   {:text "bored"}
   {:text "hopeful"}
   {:text "sad"}
   {:text "happy"}
   {:text "forlorn"}
   {:text "angry"}])

(def adverbs
  [{:text "carefully"}
   {:text "wistfully"}
   {:text "uncertainly"}
   {:text "willfully"}
   {:text "lustfully"}
   {:text "warily"}
   {:text "bravely"}
   {:text "sadly"}
   {:text "happily"}
   {:text "balefully"}])

(def scents
  [{:text "acrid"}
   {:text "sweet"}
   {:text "sour"}
   {:text "rotten"}
   {:text "nice"}
   {:text "foul"}
   {:text "like feet"}
   {:text "like your grandfather's hair cream"}
   {:text "bitter"}
   {:text "smoky"}
   {:text "gross"}
   {:text "pleasant"}])

(def diagnoses
  [{:text "feeling great"}
   {:text "feeling gross"}
   {:text "absurdly sticky"}
   {:text "lightly wounded"}
   {:text "moderately wounded"}
   {:text "heavily wounded"}
   {:text "near death"}
   {:text "sleepy"}
   {:text "drunk"}
   {:text "stoned"}
   {:text "confused"}
   {:text "hungry"}
   {:text "thirsty"}
   {:text "temporarily blind"}
   {:text "temporarily deaf"}
   {:text "covered in bees"}])

(def foods
  [{:text "burrito"
    :article "a"}
   
   {:text "pancakes"
    :article "some"}

   {:text "cake"
    :article "a"}
   
   {:text "cake"
    :article "a slice of"}

   {:text "kumquat"
    :article "a"}
   
   {:text "salad"
    :article "a"}

   {:text "Rice Chex"
    :article "a bowl of"}

   {:text "Reese's Peanut Butter Cup"
    :article "a"}

   {:text "apple pocket"
    :article "an"}

   {:text "block of cheese"
    :article "a"}

   {:text "wedge of cheese with some mold on it"
    :article "a"}

   {:text "slice of fried spam"
    :article "a"}

   {:text "moist churro"
    :article "a"}

   {:text "chocolate bobka"
    :article "a"}
   
   {:text "Cinnabon"
    :article "a"}
   
   {:text "duck confit"
    :article "some"}
   
   {:text "pasta"
    :article "some"}
   
   {:text "uncooked rice"
    :article "some"}
   
   {:text "Fritos"
    :article "some"}
   
   {:text "sushi"
    :article "some"}

   {:text "apple cinnamon Pop Tart"
    :article "an"}
   
   {:text "old fruit leather"
    :article "some"}])

(def drinks
  [{:text "cup of steaming gravy"
    :article "a"}
   
   {:text "milk"
    :article "a gallon of"}

   {:text "tea"
    :article "some"}

   {:text "soda"
    :article "some"}

   {:text "water"
    :article "some"}

   {:text "beef broth"
    :article "some"}

   {:text "scotch"
    :article "a"}])

(def garments
  [{:text "hat"
    :article "a"}
   
   {:text "pants"
    :article "some"}
   
   {:text "shirt"
    :article "a"}
   
   {:text "gloves"
    :article "some"}
   
   {:text "shoes"
    :article "some"}
   
   {:text "belt"
    :article "a"}
   
   {:text "socks"
    :article "some"}
   
   {:text "coat"
    :article "a"}
   
   {:text "jacket"
    :article "a"}
   
   {:text "underwear"
    :article "some"}
   
   {:text "dress"
    :article "a"}
   
   {:text "skirt"
    :article "a"}
   
   {:text "sweater"
    :article "a"}
   
   {:text "watch"
    :article "a"}])

(def items
  [{:text "skinny jeans"
    :article "a pair of"}

   {:text "Breakin' 2: Electric Boogaloo"
    :article "a Laserdisc copy of"}

   {:text "magic scroll"
    :article "a"}

   {:text "no tea"}

   {:text "slide rule"
    :article "a"}

   {:text "pinecone"
    :article "a"}

   {:text "sweat-incrusted trilby"
    :article "a"}
   
   {:text "vitamins"
    :plural true
    :article "some"}
   
   {:text "bucket of corks"
    :article "a"}
   
   {:text "jean shorts"
    :article "a pair of"}
   
   {:text "non-Euclidian Lego"
    :article "a"}
   
   {:text "spray-on bacon"
    :article "a can of"}
   
   {:text "spackle"
    :article "a can of"}
   
   {:text "unfamiliar briefcase"
    :article "an"}
   
   {:text "towel from the Las Vegas Radisson"
    :article "a"}
   
   {:text "receipt from a bunny outfit rental"
    :article "a"}
   
   {:text "floppy disk"
    :article "a"}
   
   {:text "pencil"
    :article "a"}
   
   {:text "lantern"
    :article "a"}
   
   {:text "elven sword"
    :article "an"}
   
   {:text "books"
    :article "some"}
   
   {:text "movie ticket"
    :article "a"}
   
   {:text "newspaper"
    :article "a"}
   
   {:text "kitten"
    :article "a"}
   
   {:text "puppy"
    :article "a"}
   
   {:text "bag of potatoes"
    :article "a"}
   
   {:text "bag of rice"
    :article "a"}
   
   {:text "giant styrofoam peanut"
    :article "a"}
   
   {:text "phone book"
    :article "a"}
   
   {:text "pyramid of tennis balls"
    :article "a"}
   
   {:text "deflated soccer ball"
    :article "a"}
   
   {:text "fourth grade report card"
    :article "your"}
   
   {:text "half-eaten sandwich"
    :article "a"}
   
   {:text "signed photograph of Richard Moll"
    :article "a"}
   
   {:text "hipster t-shirt"
    :article "a"}
   
   {:text "pile of discarded puppets"
    :article "a"}
   
   {:text "wet Lincoln Log"
    :article "a"}
   
   {:text "VHS tape covered in blood"
    :article "a"}])

(def animals
  [{:text "kitten"
    :article "a"
    :sounds ["purrs" "meows" "growls"]
    :adjectives ["purring" "meowing" "growling"]}

   {:text "cat"
    :article "a"
    :sounds ["purrs" "meows" "growls"]
    :adjectives ["purring" "meowing" "growling"]}

   {:text "puppy"
    :article "a"
    :sounds ["pants" "barks" "growls" "whimpers"]
    :adjectives ["panting" "barking" "growling" "whimpering"]}

   {:text "duck"
    :article "a"
    :sounds ["quacks"]
    :adjectives ["quacking"]}

   {:text "marmot"
    :article "a"}

   {:text "tiger"
    :article "a"
    :sounds ["roars"]
    :adjectives ["roaring"]}

   {:text "hamster"
    :article "a"}

   {:text "gerbil"
    :article "a"}

   {:text "hedgehog"
    :article "a"}])

(def noises 
  [{:text "foghorn"
    :article "a"}

   {:text "laughter"
    :article "some"}

   {:text "laughing"
    :article "somebody"}

   {:text "chuckling"
    :article "someone"}

   {:text "cackling"
    :article "someone"}

   {:text "crying"
    :article "someone"}

   {:text "sobbing"
    :article "someone"}

   {:text "sneeze"
    :article "a"}

   {:text "wolves howling"}

   {:text "ice cream truck"
    :article "an"}

   {:text "door slam"
    :article "a"}

   {:text "sinister chuckle"
    :article "a"}])

(def disasters
  [{:text "fire"
    :article "a"}

   {:text "tornado"
    :article "a"}

   {:text "hurricane"
    :article "a"}

   {:text "flood"
    :article "a"}

   {:text "tsunami"
    :article "a"}

   {:text "landslide"
    :article "a"}

   {:text "avalanche"
    :article "an"}

   {:text "radioactive leak"
    :article "a"}

   {:text "lava flow"
    :article "a"}

   {:text "sandstorm"
    :article "a"}

   {:text "lightning strike"
    :article "a"}

   {:text "plague of locusts"
    :article "a"}

   {:text "snowstorm"
    :article "a"}

   {:text "duststorm"
    :article "a"}])

(def games
  [{:text "Agricola"
    :type :tabletop}
   
   {:text "Advanced Squad Leader"
    :type :tabletop}

   {:text "Carcassonne"
    :type :tabletop}

   {:text "World in Flames"
    :type :tabletop}

   {:text "Monopoly"
    :type :tabletop}

   {:text "World of Warcraft"
    :type :video}

   {:text "Civilization V"
    :type :video}

   {:text "Grand Theft Auto V"
    :type :video}])

(def collections
  ["event-types"
   "location-events"
   "action-events"
   "secondary-events"
   "tertiary-events"
   "actor-actions"
   "locations"
   "dialogues"
   "intonations"
   "books"
   "directions"
   "persons"
   "actions"
   "adjectives"
   "adverbs"
   "scents"
   "diagnoses"
   "foods"
   "drinks"
   "signs"
   "garments"
   "items"
   "animals"
   "noises"
   "games"
   "disasters"])

(defn- encode-collection-name [s] (string/replace s #"-" "_"))

(defn clear-db-collection 
  "Empty a collection of its documents."
  [name]
  (let [db (:db (connect-via-uri (env :database-uri)))]
    (remove db (encode-collection-name name))))

(defn clear-db-collections 
  "Empty a set of collections of their documents."
  []
  (doseq [c collections] 
    (println "Removing" c "...")
    (clear-db-collection c)))

(defn add-db-collection 
  "Adds a collection to the database."
  [name]
  (let [db (:db (connect-via-uri (env :database-uri)))]
    (insert-batch db 
                  (encode-collection-name name) 
                  @(-> name symbol resolve))))

(defn add-db-collections 
  "Adds a set of collections to the database."
  []
  (doseq [c collections]
    (println "Adding" c "...")
    (add-db-collection c)))

(defn refresh-collection 
  "Clears out the collection and adds all new entries."
  [name]
  (clear-db-collection name)
  (add-db-collection name))

(defn rebuild-database 
  "Empties the database and adds again all of the collections."
  []
  (clear-db-collections)
  (add-db-collections))

(defn read-collection
  [name]
  (let [db (:db (connect-via-uri (env :database-uri)))]
    (with-collection db name
      (find {}))))
