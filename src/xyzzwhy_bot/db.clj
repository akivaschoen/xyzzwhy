 (ns xyzzwhy-bot.db
  (:refer-clojure :exclude [remove])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri]]
            [monger.collection :refer [insert-batch remove]]))

(def event-types
  [{:text "location-event"}
   {:text "action-event"}])

(def location-events
  [{:text "You have entered {{room}}."}
   {:text "You are standing {{direction}} of {{room}}."}
   {:text "You stumble into {{room}}."}
   {:text "You come across {{room}}."}
   {:text "You are {{room-with-prep}}."}
   {:text "You wake up from an odd dream. You are {{room-with-prep}}."}
   {:text "You open the secret door only to see {{room}}."}
   {:text "You find yourself {{room-with-prep}}."}
   {:text "You start doing the worm until you find yourself {{room-with-prep}}."}
   {:text "You wake up {{room-with-prep}}."}
   {:text "You climb down the tree and find yourself {{room-with-prep}}."}
   {:text "The taxi driver randomly drops you off {{room-with-prep}}."}
   {:text "The fog clears and you find yourself {{room-with-prep}}."}
   {:text "You jump out of a moving car, roll down a hill, and find yourself {{room-with-prep}}."}
   {:text "After walking for a long time, you find yourself {{room-with-prep}}."}
   {:text "You find your way blindly and end up {{room-with-prep}}."}
   {:text "No matter how hard you try, you still end up {{room-with-prep}}."}
   {:text "You climb out of the treasure chest. You are now {{room-with-prep}}."}
   {:text "You come to {{room-with-prep}}."}
   {:text "You follow a winding path only to find yourself {{room-with-prep}}."}
   {:text "The elevator doors open to reveal {{room}}."}
   {:text "The trapdoor drops open beneath you and you land {{room-with-prep}}."}
   {:text "You get tangled up in a revolving door. You stumble out into {{room}}."}
   {:text "After scrambling through some dense underbrush, you find yourself {{room-with-prep}}."}
   {:text "Hands on your hips, you survey {{room}} {{adverb}}."}
   {:text "You have reached a dead-end. You moonwalk away."}])

(def action-events
  [{:text "You awake from a nightmare. You saw yourself {{room-with-prep}}. The corpse of {{person}} was there, holding {{item}}."}
   {:text "You grab {{item}}, hoping {{person}} doesn't notice."}
   {:text "The radio crackles to life. 'Mayday, mayday, it's {{person}} calling. We're in trouble. We need assistance. Mayday, mayday.'"}
   {:text "{{actor}} drops {{item}}, looks at you {{adverb}}, then leaves."}
   {:text "Suddenly, {{actor}} {{action}} you."}
   {:text "{{actor}} {{action}} {{actor}}."}
   {:text "{{actor}} {{action}} you."}
   {:text "{{actor}} drops {{item}} here."}
   {:text "{{person}} starts breakdancing and won't stop no matter how much you scream."}
   {:text "{{actor}} attacks you and knocks you out! You awake sometime later {{room-with-prep}}."}
   {:text "{{person}} appears in a puff of smoke and shouts, 'You will never see your {{item}} again!'"}
   {:text "You startle {{person}} who drops {{item}} and runs away."}
   {:text "{{person}} slams down a half-empty glass of bourbon. 'All this nonsense about {{item}} needs to stop! I can't take it anymore!'"}
   {:text "{{person}} suddenly shrieks."}
   {:text "You get tired of waiting for your Uber and decide to walk to {{room}} instead."}
   {:text "The phone rings. {{person}} stares at it {{adverb}}. You refuse to answer it. Eventually the ringing stops."}
   {:text "You start eating {{food}} and don't stop until you're done."}
   {:text "You eat {{food}}."}
   {:text "You eat {{food}}. {{actor}} looks on {{adverb}}."}
   {:text "You feel a little famished so you eat {{food}}."}
   {:text "You take a sip of {{drink}}."}
   {:text "You check your inventory. You are empty-handed."}
   {:text "You check your inventory. You are carrying {{item}}, {{item}}, and {{item}}."}
   {:text "You check your inventory. You have {{item}} and {{item}}."}
   {:text "You open up {{book}}. Someone has scribbled all over the margins. You throw it down on the floor in disgust."}
   {:text "You open up {{book}}. Someone has left a recipe for beef stew inside."}
   {:text "You open up {{book}}. You read a bit before tossing it over your shoulder and then doing the electric slide."}
   {:text "{{actor}} suddenly appears out of the shadows, hisses at you, then scrambles away like a spider."}
   {:text "{{actor}} picks up {{item}}."}
   {:text "An overhead loudspeaker crackles to life, 'Citizen! Report immediately to the nearest self-incrimination booth.'"}
   {:text "You start spinning around and around while {{person}} claps and cheers."}
   {:text "{{person}} is calling from {{room}} asking for {{item}}."}
   {:text "You peek out the window. {{person}} is messing around with your mailbox. You crouch in fear."}
   {:text "In the distance, you hear {{person}} let the bass drop."}
   {:text "You check your health: you are {{diagnose}}."}])

(def secondary-events 
  [{:text "You see {{item}} here."}
   {:text "You see {{item}} here. It looks oddly familiar."}
   {:text "There is {{item}} here."}
   {:text "You pick up {{item}}. Was this here before?"}
   {:text "You pick up {{item}}."}
   {:text "You drop {{item}}."}
   {:text "You find {{item}} here but decide to leave it alone."}
   {:text "{{actor}} is here."}
   {:text "{{actor}} is here{{actor-action}}"}
   {:text "You find {{actor}}{{actor-action}}"}
   {:text "{{person}} {{dialogue}}"}
   {:text "{{person}} {{dialogue}}"}
   {:text "{{actor}} is here searching for {{item}}."}
   {:text "{{actor}} is here hoping to run into {{actor}}."}
   {:text "{{actor}} has been following you."}
   {:text "A hollow voice intones, '{{intonation}}'"}
   {:text "A hollow voice intones, '{{intonation}}'"}
   {:text "Something smells {{scent}} here."}
   {:text "You hear {{noise}} in the distance."}
   {:text "You hear the sound of {{noise}} nearby."}
   {:text "The wind howls in the distance."}
   {:text "It appears abandoned."}
   {:text "Someone has been here recently."}
   {:text "There are fresh footprints here."}
   {:text "It seems that no one has been here for a long time."}
   {:text "Someone has attached marionnette wires to your hands, feet, and head."}
   {:text "Someone has left a running bulldozer here."}
   {:text "The words 'eat dulp' are spray-painted on the wall here.'"}
   {:text "There has been significant damage from {{disaster}}."}
   {:text "You see a sign here. On it is written '{{sign}}'"}])

(def tertiary-events 
  [{:text "You aren't wearing any clothes."}
   {:text "Your shoes are on the wrong feet."}
   {:text "Your tie feels uneven."}
   {:text "You're not wearing any underwear."}
   {:text "You do a little jig and then whistle."}
   {:text "You clap once."}
   {:text "You have socks on your hands."}
   {:text "You feel nervous."}
   {:text "You feel anxious."}
   {:text "You feel cold."}
   {:text "You feel warm."}
   {:text "You blink really slowly."}
   {:text "You yawn."}
   {:text "You begin to smile uncontrollably."}
   {:text "You wish you had your grandpappy's harmonica."}
   {:text "You are starting to feel sleepy."}
   {:text "You think about brushing your hair but change your mind."}
   {:text "You spend a few moments thinking fondly about your teeth."}
   {:text "You have no idea how these rope burns got on your wrists."}
   {:text "You feel as if you're being followed."}
   {:text "A warm breeze blows by."}
   {:text "A cool breeze blows by."}
   {:text "It starts to rain."}
   {:text "A basketball bounces by."}
   {:text "You spot a balloon stuck in a tree."}
   {:text "Somehow, you've lost your {{garment}}."}
   {:text "You hear someone nearby typing away on a manual typewriter."}
   {:text "You are starting to feel hungry."}])

(def actor-actions
  [{:text " looking {{adjective}}."}
   {:text " dancing furiously."}
   {:text " shouting at an imaginary helicopter."}
   {:text " doing the Kenosha Kid."}
   {:text " thinking {{adverb}} about {{actor}}."}
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
   {:text ", munching on {{food}}."}
   {:text ", pretending to be invisible."}
   {:text ", having a coughing fit."}
   {:text ", having a sneezing fit."}
   {:text ", being menaced by {{animal}}."}
   {:text ", ready to start some shit."}
   {:text ", examining {{item}} with great confusion."}])

(def rooms
  [{:text "tire fire" 
    :type :exterior
    :article "a" 
    :preps ["at" "near" "behind" "in front of"]
    :descriptions ["The air here is black with despair and entropy."
                   "The sky is darkened by the hellish smoke of the endless burn."
                   "These tires are no longer the things on your car that make contact with the road."]}
   
   {:text "dildo bonfire" 
    :type :exterior
    :article "a" 
    :preps ["at" "near" "behind" "in front of"]
    :descriptions ["Someone has piled up a collection of pleasuring devices, now ablaze."
                   "Surely there had to hae been a better way to punish these plastic torpedos."
                   "The air is dense with the echoes of unreached orgasms and epic frustrations."]}

   {:text "maze of twisty passages, all alike"
    :type :interior
    :article "a"
    :preps ["in"]}

   {:text "Burning Man"
    :type :exterior
    :preps ["at"]}

   {:text "Shrim Healing Center"
    :type :exterior
    :article "a"
    :preps ["in" "at" "in front of" "behind"]
    :descriptions ["In the store window is an array of old television sets, all blackly inert."
                   "From somewhere within the building, you hear the sound of repulsed joy."
                   "The healing center looks like it has been condemned. The door is boarded up."]}

   {:text "quicksand"
    :type :exterior
    :article "some"
    :preps ["in" "near"]
    :descriptions ["Briefly, you see a fin rise up and cruise back and forth."
                   "You see a giant bubble rise up and burst; a fart from the great unknown depths."
                   "Oddly, this quicksand smells like freshly cooked oatmeal."]}

   {:text "swimming pool" 
    :type :exterior
    :article "a"
    :preps ["in" "at" "near"]
    :descriptions ["The surface of the pool is almost entirely still. You are afraid to disturb it."
                   "The water has turned slightly murky; it does not look inviting."
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
    :descriptions ["A rocky ravine stretches out in front of you, meandering as if drunk."
                   "The ravine has been nearly choked to death by an avalanche at the north end."
                   "The walls of the ravine are treacherous. A trickle of water flows fitfully below."]}

   {:text "ditch" 
    :type :exterior
    :article "a" 
    :preps ["in"]
    :desriptions ["The dusty stench of aged sewage rises up like a stomach-crushing wraith."
                  "The ditch is completely blocked here by a giant boulder. How did it get here?"
                  "You are standing straddling a trickle of water running down the middle of the ditch."]}

   {:text "dump" 
    :type :exterior
    :article "the"
    :preps ["at" "near" "behind" "in front of"]
    :descriptions ["In the distance, you see women searching through spires of rubbish for treasure."
                   "You are standing at the crest of hill of trash. It shifts dangerously beneath your feet."
                   "You are wandering through a labyrinth of stinking garbage."]}

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
    :preps ["in" "near" "behind" "in front of"]}

   {:text "park restroom stall" 
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["The door has been torn off its hinges and the walls are covered with violent scratches."
                   "Unfortunately, the toilet recently vomited up at least five gallons of excrement and dreams."
                   "A lingering scents of lemon and Lysol haunts the air here."
                   "Someone has scratched your name and phone number above the toilet paper dispenser."]}

   {:text "all-you-can-eat buffet" 
    :type :interior
    :article "an"
    :preps ["at"]
    :descriptions ["Before you is a grid of delicious choices, all unhealthy, all alluring. Steam crowds the air."
                   "You find yourself faced with a dizzying array of gluten-free, vegan choices. You leave immediately."
                   "It's in complete disarray and hasn't been tended for some time. Most of the trays are empty."]}

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
    :descriptions ["It hasn't been cleaned in a long time; it's a mess. There's a pleasantly disgusting smell here."
                   "It's small and lightly furnished. The bed is unmade. Has someone been sleeping here?"
                   "It's a typical bedroom. There's a pile of laundry in one corner and a computer desk in the other."] }

   {:text "McDonald's" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "White Castle" 
    :type :exterior
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "Taco Bell" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:text "dark area" 
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["It is pitch black here. You're likely to be eaten by {{actor}}"]}

   {:text "breezy cave" 
    :type :exterior
    :article "a"
    :preps ["in" "near" "in front of"]
    :descriptions ["Before you a narrow cave descends into the darkness. There's a constant breeze rising up from the depths."
                  "A wide and low cave wanders {{direction}}-{{direction}} here."
                  "Here the cave winds up precariously a natural stair. The cave seems to be breathing rapidly."]}

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
    :descriptions ["You are standing in a shallow riverbed which has long ago dried up."
                   "A deep channel runs through the riverbed through which brackish water flows fitfully."
                   "The riverbed here is mostly dry, the flow of the water almost completely blocked by a beaver dams upstream."]}
   
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
    :preps ["in"]}

   {:text "trunk of a car" 
    :type :interior
    :article "the"
    :preps ["in"]}

   {:text "coffin" 
    :type :interior
    :article "a"
    :preps ["in" "near" "in front of"]}

   {:text "haunted house" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]
    :descriptions ["At the top of the hill, the house shrugs under its own entropy."
                   "An orange light wanders from window to window."
                   "The antebellum abode, white in its gaudy shame of elegance, has been overgrown by kudzu and rotting vines."]
    }

   {:text "graveyard" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]
    :descriptions ["A lazy mist wanders aimlessly amongst the shifted tombstones. A cold light spills down from behind a tree."
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
    :preps ["in" "near" "behind" "in front of" "underneath"] }

   {:text "meeting" 
    :type :interior
    :article "a"
    :preps ["in"]
    :descriptions ["The room is comically overwhelmed by tripod stands crowded with colorful charts."
                   "The room is empty. The projector is on, showing random photos of cats at play."
                   "The table is covered, end-to-end, by neat stacks of donuts of various heights."]}

   {:text "Luby's" 
    :type :exterior
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}])

(def dialogues
  [{:text "chants, 'It's time to pay the price.'"}
   {:text "says, 'I've been waiting for you.'"}
   {:text "says, 'I can't find my heirloom clown suit."}
   {:text "says, 'I can't find my {{garment}}.'"}
   {:text "says, 'No money? No hamburger!"}
   {:text "says, 'It's like drinking a meatloaf!'"}
   {:text "whispers, 'I've always wanted to be a creepy uncle.'"}
   {:text "whispers, 'When you hear the circus music, you will know it is time.'"}
   {:text "whispers, 'Shockerrrrrrr...'"}
   {:text "whispers, 'There squats the brown clown.'"}
   {:text "asks, 'Have you ever seen an elephant throw up?'"}
   {:text "asks, 'Why am I holding this pitchfork?'"}
   {:text "asks, 'How long is a man?'"}
   {:text "asks, 'Where have you been?'"}
   {:text "says, 'Took you long enough.'"}
   {:text "says, 'I'm a brown-belt in speed tai chi."}
   {:text "asks, 'Can I have a hug?'"}
   {:text "says, 'If you asked me to have sex with you, I wouldn't say \"no\"."}
   {:text "asks, 'Are you following me?'"}
   {:text "shrieks, 'What's this shit I keep hearing about erections?!'"}
   {:text "shrieks, 'I'm living on the edge!'"}
   {:text "shrieks, 'Boiled soil!"}
   {:text "shouts, 'You can't go up against city hall!'"}
   {:text "shouts, 'You can't fold a cat!'"}
   {:text "screams, 'They're having a brownout in Lagos!'"}
   {:text "mumbles, 'You can't go up against city hall.'"}
   {:text "mumbles, 'One day I'm going to burn this place to the ground.'"}
   {:text "mumbles, 'Skrillex ruined it all for everybody."}
   {:text "asks, 'Does it smell like {{food}} in here to you?'"}])

(def intonations
  [{:text "Toast goes in the toaster."}
   {:text "When you hear the bells, you will know it is time."}
   {:text "For those who can make the journey, there is a place."}
   {:text "Plugh."}
   {:text "Your pilikia is all pau."}
   {:text "The owls are not what they seem."}
   {:text "Puch."}
   {:text "Guch."}
   {:text "Porluch."}
   {:text "Sorry but it couldn't be helped."}
   {:text "Clean up in aisle 8A."}
   {:text "Rabbit feces."}
   {:text "Consider deeply the baked ham."}
   {:text "You can't go up against city hall."}])

(def signs
  [{:text "Burma shave!"}
   {:text "For those who can make the journey, there is a place."}
   {:text "Here lies Hammerdog, a dog made of hammers."}
   {:text "Here lies Knifekitten, a kitten made of knives."}
   {:text "When you're not reading this, it's written in Spanish."}
   {:text "Now you know how hard it is to say 'Irish wristwatch'."}])

(def books
  [{:text "the Bible"
    :article "a copy of"}

   {:text "Catcher in the Rye"
    :article "a copy of"}

   {:text "Infinite Jest"
    :article "a copy of"}

   {:text "Gravity's Rainbow"
    :article "a copy of"}

   {:text "A Prayer for Owen Meany"
    :article "a copy of"}

   {:text "Hitchhiker's Guide to the Galaxy"
    :article "a copy of"}])

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

   {:text "Gene Shalit"
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

   {:text "Hilary Clinton"
    :gender :female}

   {:text "Craig T. Nelson"
    :gender :male}

   {:text "Thomas Pynchon"
    :gender :male}

   {:text "@akiva"
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
   
   {:text "salad"
    :article "a"}

   {:text "Rice Chex"
    :article "a bowl of"}

   {:text "Reese's Peanut Butter Cup"
    :article "a"}

   {:text "apple pocket"
    :article "an"}

   {:text "apple cinnamon Pop Tart"
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

(def collection-list
  (list
    "event-types"
    "location-events"
    "secondary-events"
    "tertiary-events"
    "actor-actions"
    "rooms"
    "dialogues"
    "signs"
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
    "garments"
    "items"
    "animals"
    "noises"
    "disasters"))

(def collections
  ["event-types"
   "location-events"
    "action-events"
    "secondary-events"
    "tertiary-events"
    "actor-actions"
    "rooms"
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
    "garments"
    "items"
    "animals"
    "noises"
    "disasters"])

(defn- encode-collection-name [s] (string/replace s #"-" "_"))

(defn clear-db-collection 
  "Empty a collection of its documents."
  [name]
  (let [{:keys [conn db]} (connect-via-uri (env :mongolab-uri))]
    (remove db (encode-collection-name name))))

(defn clear-db-collections 
  "Empty a set of collections of their documents."
  []
  (doseq [c collections] 
    (clear-db-collection c)))

(defn add-db-collection 
  "Adds a collection to the database."
  [name]
  (let [{:keys [conn db]} (connect-via-uri (env :mongolab-uri))]
    (insert-batch db 
                  (encode-collection-name name) 
                  @(-> name symbol resolve))))

(defn add-db-collections 
  "Adds a set of collections to the database."
  []
  (doseq [c collections]
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
