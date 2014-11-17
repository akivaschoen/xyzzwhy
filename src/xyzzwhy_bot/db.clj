 (ns xyzzwhy-bot.db
  (:refer-clojure :exclude [remove])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri]]
            [monger.collection :refer [insert-batch remove]]))

(defn- encode-collection-name [s] (string/replace s #"-" "_"))

(def event-types
  [{:name "location-event"}
   {:name "action-event"}])

(def location-events
  [{:name "You have entered {{room}}."}
   {:name "You jump out of a moving car, roll down a hill, and find yourself {{room-with-prep}}."}
   {:name "You are standing {{direction}} of {{room}}."}
   {:name "You stumble into {{room}}."}
   {:name "You come across {{room}}."}
   {:name "You are {{room-with-prep}}."}
   {:name "This is {{room}}."}
   {:name "You open the secret door only to see {{room}}."}
   {:name "You find yourself {{room-with-prep}}."}
   {:name "You start doing the worm until you find yourself {{room-with-prep}}."}
   {:name "You wake up from an odd dream unsure of where you are."}
   {:name "You wake up {{room-with-prep}}."}
   {:name "You climb down the tree and find yourself {{room-with-prep}}."}
   {:name "The taxi driver randomly drops you off {{room-with-prep}}."}
   {:name "The fog clears and you find yourself {{room-with-prep}}."}
   {:name "After walking for a long time, you find yourself {{room-with-prep}}."}
   {:name "You find your way blindly and end up {{room-with-prep}}."}
   {:name "No matter how hard you try, you still end up {{room-with-prep}}."}
   {:name "You climb out of the treasure chest. You are now {{room-with-prep}}."}
   {:name "You come to {{room-with-prep}}."}
   {:name "You follow a winding path only to find yourself {{room-with-prep}}."}
   {:name "The elevator doors open to reveal {{room}}."}
   {:name "The trapdoor drops open beneath you and you land {{room-with-prep}}."}
   {:name "You get tangled up in a revolving door. You stumble out into {{room}}."}
   {:name "After scrambling through some dense underbrush, you find yourself {{room-with-prep}}."}
   {:name "Hands on your hips, you survey {{room}} {{adverb}}."}
   ])

(def action-events
  [{:name "You awake from a nightmare. You saw yourself {{room-with-prep}}. The corpse of {{person}} was there, holding {{item}}."}
   {:name "You grab {{item}}, hoping {{person}} doesn't notice."}
   {:name "You pick up {{item}}."}
   {:name "You drop {{item}}."}
   {:name "The radio crackles to life. 'Mayday, mayday, it's {{person}} calling. We're in trouble. We need assistance. Mayday, mayday.'"}
   {:name "You pick up {{item}}. Was this here before?"}
   {:name "You pick up {{item}}."}
   {:name "You find {{item}} but decide to leave it alone."}
   {:name "{{actor}} drops {{item}}, looks at you {{adverb}}, then leaves."}
   {:name "Suddenly, {{actor}} {{action}} you!"}
   {:name "{{actor}} {{action}} {{actor}}!"}
   {:name "{{actor}} {{action}} you!"}
   {:name "{{actor}} drops {{item}} here."}
   {:name "{{person}} starts breakdancing and won't stop no matter how much you scream."}
   {:name "{{actor}} attacks you and knocks you out! You awake sometime later in {{room}}."}
   {:name "{{person}} appears in a puff of smoke and shouts, 'You will never go to {{room}} again!'"}
   {:name "You startle {{person}} who drops {{item}} and then runs away."}
   {:name "{{person}} slams down a half-empty empty glass of bourbon. 'All this nonsense about {{item}} needs to stop! I can't take it anymore!'"}
   {:name "{{person}} suddenly shrieks."}
   {:name "{{person}} shouts, 'You can't go up against city hall!'"}
   {:name "You get tired of waiting for your Uber and decide to walk to {{room}} instead."}
   {:name "The phone rings. {{person}} stares at it {{adverb}}. You refuse to answer it. Eventually the ringing stops."}
   {:name "You start eating {{food}} and don't stop until you're done."}
   {:name "You start to eat {{food}} but it doesn't taste very good."}
   {:name "You eat {{food}}. {{actor}} looks on {{adverb}}."}
   {:name "You feel a little famished so you eat {{food}}."}
   {:name "You take a sip of {{drink}}."}
   {:name "You check your inventory. You are empty-handed."}
   {:name "You check your inventory. You are carrying {{item}}, {{item}}, and {{item}}."}
   {:name "You check your inventory. You have {{item}} and {{item}}."}
   {:name "You open up your copy of {{book}}. Someone has scribbled all over the margins. You throw it down on the floor in disgust."}
   {:name "{{actor}} suddenly appears out of the shadows, hisses at you, then scrambles away like a spider."}
   {:name "{{actor}} picks up {{item}}."}
   {:name "You start spinning around and around while {{person}} claps and cheers."}
   {:name "{{person}} is calling from {{room}} asking for {{item}}."}
   {:name "You peek out the window. {{person}} is messing around with your mailbox. You crouch in fear."}
   {:name "In the distance, you hear {{person}} let the bass drop."}
   {:name "You check your health: you are {{diagnose}}."}])

(def secondary-events 
  [{:name "You see {{item}} here."}
   {:name "You see {{item}} here. It looks oddly familiar."}
   {:name "There is {{item}} here."}
   {:name "{{actor}} is here."}
   {:name "{{actor}} is here{{actor-action}}"}
   {:name "You find {{actor}}{{actor-action}}"}
   {:name "{{person}} {{dialogue}}"}
   {:name "{{actor}} is here searching for {{item}}."}
   {:name "{{actor}} is here hoping to run into {{actor}}."}
   {:name "Something smells {{scent}} here."}
   {:name "You hear {{noise}} in the distance."}
   {:name "You hear the sound of {{noise}} nearby."}
   {:name "The wind howls in the distance."}
   {:name "It appears abandoned."}
   {:name "Someone has been here recently."}
   {:name "There are fresh footprints here."}
   {:name "It seems that no one has been here for a long time."}
   {:name "Someone has attached marionnette wires to your hands, feet, and head."}
   {:name "Someone has left a running bulldozer here."}
   {:name "The words 'eat dulp' are spray-painted on the wall here.'"}
   {:name "There has been significant damage from {{disaster}}."}])

(def tertiary-events 
  [{:name "You aren't wearing any clothes."}
   {:name "Your shoes are on the wrong feet."}
   {:name "Your tie feels uneven."}
   {:name "You're not wearing any underwear."}
   {:name "You do a little jig and then whistle."}
   {:name "You clap once."}
   {:name "You have socks on your hands."}
   {:name "You feel nervous."}
   {:name "You feel anxious."}
   {:name "You feel cold."}
   {:name "You feel warm."}
   {:name "You blink really slowly."}
   {:name "You yawn."}
   {:name "You begin to smile uncontrollably."}
   {:name "You wish you had your grandpappy's harmonica."}
   {:name "You are starting to feel sleepy."}
   {:name "You think about brushing your hair but change your mind."}
   {:name "You spend a few moments thinking fondly about your teeth."}
   {:name "You have no idea how these rope burns got on your wrists."}
   {:name "You feel as if you're being followed."}
   {:name "A warm breeze blows by."}
   {:name "A cool breeze blows by."}
   {:name "It starts to rain."}
   {:name "A basketball bounces by."}
   {:name "You spot a balloon stuck in a tree."}
   {:name "Somehow, you've lost your {{garment}}."}
   {:name "You hear someone breaking eggs and sobbing nearby."}
   {:name "You hear someone whisking heavy cream while laughing nearby."}
   {:name "You are starting to feel hungry."}])

(def actor-actions
  [{:name "looking {{adjective}}."}
   {:name "doing the Kenosha Kid."}
   {:name "thinking {{adverb}} about {{actor}}."}
   {:name "being chased around by a bee."}
   {:name "organizing matches."}
   {:name "juggling some balls."}
   {:name "dancing in a little circle."}
   {:name "stooping up and down like a rapper in concert."}
   {:name "drooling uncontrollably."}
   {:name ", hiding under a table."}
   {:name ", hiding under a sofa."}
   {:name ", munching on {{food}}."}
   {:name ", pretending to be invisible."}
   {:name ", having a coughing fit."}
   {:name ", having a sneezing fit."}
   {:name ", being menaced by {{animal}}."}
   {:name ", ready to start some shit."}
   {:name ", examining {{item}} with great confusion."}])

(def rooms
  [{:name "tire fire" 
    :article "a" 
    :long_name "{{adverb}} burning tire fire"
    :preps ["at" "near" "behind" "in front of"] }
   
   {:name "dildo bonfire" 
    :article "a" 
    :long_name "{{adverb}} burning dildo bonfire"
    :preps ["at" "near" "behind" "in front of"] }

   {:name "maze of twisty passages, all alike"
    :article "a"
    :preps ["in"]}

   {:name "Burning Man"
    :preps ["at"]}

   {:name "Shrim Healing Center"
    :article "a"
    :preps ["in" "at" "in front of" "behind"]}

   {:name "quicksand"
    :long_name "a pool of quicksand"
    :article "some"
    :preps ["in" "near"]}

   {:name "swimming pool" 
    :article "a"
    :preps ["in" "at" "near"]}

   {:name "sauna" 
    :article "a" 
    :preps ["in" "near"]} 
   
   {:name "New York Public Library" 
    :article "the"
    :preps ["at" "near" "behind" "in front of"]}

   {:name "ravine" 
    :article "a" 
    :preps ["in"]}

   {:name "ditch" 
    :article "a" 
    :preps ["in"]}

   {:name "dump" 
    :article "the"
    :preps ["at" "near" "behind" "in front of"]}

   {:name "dump truck" 
    :article "a"
    :preps ["in" "near" "behind" "in front of" "underneath"]}

   {:name "Starbucks" 
    :article "a"
    :preps ["in" "near" "behind" "in front of"]}

   {:name "park restroom stall" 
    :article "a"
    :preps ["in"]}

   {:name "all-you-can-eat buffet" 
    :article "an"
    :preps ["at" "near"]}

   {:name "grotto" 
    :article "a"
    :preps ["in" "near" "behind" "in front of"]}

   {:name "bedroom" 
    :article "your"
    :preps ["in"]}

   {:name "McDonald's" 
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:name "dark area" 
    :article "a"
    :preps ["in" "near" "in front of"]}

   {:name "breezy cave" 
    :article "a"
    :preps ["in" "near" "in front of"]}

   {:name "forest" 
    :article "a"
    :preps ["in" "near" "in front of"]}

   {:name "riverbed" 
    :article "a"
    :preps ["in" "near"]}
   
   {:name "AT&T Store" 
    :article "an"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:name "Apple Store" 
    :article "an"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:name "ballpit" 
    :article "a"
    :preps ["in" "near"]}

   {:name "airplane" 
    :article "an"
    :preps ["in"]}

   {:name "trunk of a car" 
    :article "the"
    :preps ["in"]}

   {:name "coffin" 
    :article "a"
    :preps ["in" "near" "in front of"]}

   {:name "haunted house" 
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:name "graveyard" 
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}

   {:name "playground" 
    :article "a"
    :preps ["in" "near" "behind" "in front of"]}

   {:name "pile of diapers" 
    :article "a"
    :preps ["in" "near" "behind" "in front of" "underneath"] }

   {:name "meeting" 
    :article "a"
    :preps ["in"]}

   {:name "Luby's" 
    :article "a"
    :preps ["at" "in" "near" "behind" "in front of"]}])

(def dialogues
  [{:name "says, 'I've been waiting for you.'"}
   {:name "says, 'I can't find my heirloom clown suit."}
   {:name "says, 'I can't find my {{garment}}.'"}
   {:name "whispers, 'I've always wanted to be a creepy uncle.'"}
   {:name "whispers, 'When you hear the circus music, you will know it is time.'"}
   {:name "asks, 'Why am I holding this pitchfork?'"}
   {:name "asks, 'How long is a man?'"}
   {:name "asks, 'Where have you been?'"}
   {:name "says, 'Took you long enough.'"}
   {:name "asks, 'Can I have a hug?'"}
   {:name "says, 'If you asked me to have sex with you, I wouldn't say \"no\"."}
   {:name "asks, 'Are you following me?'"}
   {:name "shrieks, 'What's this shit I keep hearing about erections?!'"}
   {:name "shouts, 'You can't go up against city hall!'"}
   {:name "mumbles, 'You can't go up against city hall.'"}
   {:name "mumbles, 'One day I'm going to burn this place to the ground.'"}
   {:name "asks, 'Does it smell like {{food}} in here to you?'"}])

(def books
  [{:name "the Bible"
    :article "a copy of"}

   {:name "Catcher in the Rye"
    :article "a copy of"}

   {:name "Infinite Jest"
    :article "a copy of"}

   {:name "Gravity's Rainbow"
    :article "a copy of"}

   {:name "A Prayer for Owen Meany"
    :article "a copy of"}

   {:name "Hitchhiker's Guide to the Galaxy"
    :article "a copy of"}])

(def directions
  [{:name "north"}
   {:name "northeast"}
   {:name "east"}
   {:name "southeast"}
   {:name "south"}
   {:name "southwest"}
   {:name "west"}
   {:name "northwest"}])

(def persons
  [{:name "Samuel L. Jackson"
    :gender :male}

   {:name "Frances McDormand"
    :gender :female}

   {:name "Whoopi Goldberg"
    :gender :female}

   {:name "Katy Perry"
    :gender :female}

   {:name "Lena Horne"
    :gender :female}

   {:name "Justin Bieber"
    :gender :male}

   {:name "Neil deGrasse Tyson"
    :gender :male}

   {:name "Tim Heidecker"
    :gender :male}

   {:name "Eric Wareheim"
    :gender :male}

   {:name "Jim J. Bullock"
    :gender :male}

   {:name "Johnny Cash"
    :gender :male}

   {:name "a police officer"}

   {:name "Alex Trebek"
    :gender :male}

   {:name "Craig Ferguson"
    :gender :male}

   {:name "Geoff Petersen"
    :gender :male}

   {:name "Stephen King"
    :gender :male}

   {:name "Gene Shalit"
    :gender :male}

   {:name "Catmeat Clive"
    :gender :male}

   {:name "Jorts Morgan"
    :gender :male}

   {:name "Construction Charles"
    :gender :male}

   {:name "Nancy Grace"
    :gender :female}

   {:name "Lindsay Lohan"
    :gender :female}

   {:name "Barack Obama"
    :gender :male}

   {:name "Abe Vigoda"
    :gender :male}

   {:name "Louis Gray"
    :gender :male}
   
   {:name "Brad Pitt"
    :gender :male}

   {:name "Bill Maher"
    :gender :male}

   {:name "Grace Jones"
    :gender :female}

   {:name "George W. Bush"
    :gender :male}

   {:name "your mom"}

   {:name "a bunch of kids"}

   {:name "a crowd of Yoga enthusiasts"}

   {:name "George Clooney"
    :gender :male}

   {:name "James Franco"
    :gender :male}

   {:name "Jonah Hill"
    :gender :male}

   {:name "Scarlet Johannson"
    :gender :female}

   {:name "a gas station attendant"}

   {:name "Lena Dunham"
    :gender :female}

   {:name "Hilary Clinton"
    :gender :female}

   {:name "Hilary Clinton"
    :gender :female}

   {:name "Craig T. Nelson"
    :gender :male}

   {:name "Thomas Pynchon"
    :gender :male}

   {:name "@akiva"
    :gender :male}

   {:name "@vmcny"
    :gender :male}

   {:name "@wolfpupy"
    :gender :female}

   {:name "@KamenPrime"
    :gender :male}

   {:name "@neonbubble"
    :gender :male}

   {:name "@micahwittman"
    :gender :male}

   {:name "@itafroma"
    :gender :male}

   {:name "@clive"
    :gender :male}

   {:name "Zombie Carl Sagan"
    :gender :male}])

(def actions
  [{:name "attacks"}
   {:name "ignores"}
   {:name "tickles"}
   {:name "stands uncomfortably close to"}
   {:name "pets"}
   {:name "flirts with"}])

(def adjectives
  [{:name "worried"}
   {:name "relieved"}
   {:name "aroused"}
   {:name "afraid"}
   {:name "sleepy"}
   {:name "hungry"}
   {:name "thirsty"}
   {:name "bored"}
   {:name "hopeful"}
   {:name "sad"}
   {:name "happy"}
   {:name "forlorn"}
   {:name "angry"}])

(def adverbs
  [{:name "carefully"}
   {:name "wistfully"}
   {:name "uncertainly"}
   {:name "willfully"}
   {:name "lustfully"}
   {:name "warily"}
   {:name "bravely"}
   {:name "sadly"}
   {:name "happily"}
   {:name "balefully"}])

(def scents
  [{:name "acrid"}
   {:name "sweet"}
   {:name "sour"}
   {:name "rotten"}
   {:name "nice"}
   {:name "foul"}
   {:name "like feet"}
   {:name "like your grandfather's hair cream"}
   {:name "bitter"}
   {:name "smoky"}
   {:name "gross"}
   {:name "pleasant"}])

(def diagnoses
  [{:name "feeling great"}
   {:name "lightly wounded"}
   {:name "moderately wounded"}
   {:name "heavily wounded"}
   {:name "near death"}
   {:name "sleepy"}
   {:name "drunk"}
   {:name "stoned"}
   {:name "confused"}
   {:name "hungry"}
   {:name "thirsty"}
   {:name "temporarily blind"}
   {:name "temporarily deaf"}
   {:name "covered in bees"}])

(def foods
  [{:name "burrito"
    :article "a"}
   
   {:name "salad"
    :article "a"}

   {:name "Rice Chex"
    :article "a bowl of"}

   {:name "Reese's Peanut Butter Cup"
    :article "a"}

   {:name "apple pocket"
    :article "an"}

   {:name "apple cinnamon Pop Tart"
    :article "an"}

   {:name "wedge of cheese"
    :article "a"}

   {:name "wedge of cheese with some mold on it"
    :article "a"}

   {:name "slice of fried spam"
    :article "a"}

   {:name "moist churro"
    :article "a"}

   {:name "chocolate bobka"
    :article "a"}
   
   {:name "Cinnabon"
    :article "a"}
   
   {:name "duck confit"
    :article "some"}
   
   {:name "pasta"
    :article "some"}
   
   {:name "uncooked rice"
    :article "some"}
   
   {:name "Fritos"
    :article "some"}
   
   {:name "sushi"
    :article "some"}
   
   {:name "old fruit leather"
    :article "some"}])

(def drinks
  [{:name "cup of steaming gravy"
    :article "a"}
   
   {:name "milk"
    :article "a gallon of"}

   {:name "tea"
    :article "some"}

   {:name "soda"
    :article "some"}

   {:name "water"
    :article "some"}

   {:name "beef broth"
    :article "some"}

   {:name "scotch"
    :article "a"}
   ])

(def garments
  [{:name "hat"
    :article "a"}
   
   {:name "pants"
    :article "some"}
   
   {:name "shirt"
    :article "a"}
   
   {:name "gloves"
    :article "some"}
   
   {:name "shoes"
    :article "some"}
   
   {:name "belt"
    :article "a"}
   
   {:name "socks"
    :article "some"}
   
   {:name "coat"
    :article "a"}
   
   {:name "jacket"
    :article "a"}
   
   {:name "underwear"
    :article "some"}
   
   {:name "dress"
    :article "a"}
   
   {:name "skirt"
    :article "a"}
   
   {:name "sweater"
    :article "a"}
   
   {:name "watch"
    :article "a"}])

(def items
  [{:name "skinny jeans"
    :article "a pair of"}
   
   {:name "magic scroll"
    :article "a"}

   {:name "no tea"}

   {:name "slide rule"
    :article "a"}

   {:name "pinecone"
    :article "a"}

   {:name "sweat-incrusted trilby"
    :article "a"}
   
   {:name "vitamins"
    :article "some"}
   
   {:name "bucket of corks"
    :article "a"}
   
   {:name "jean shorts"
    :article "a pair of"}
   
   {:name "non-Euclidian Lego"
    :article "a"}
   
   {:name "spray-on bacon"
    :article "a can of"}
   
   {:name "spackle"
    :article "a can of"}
   
   {:name "unfamiliar briefcase"
    :article "an"}
   
   {:name "towel from the Las Vegas Radisson"
    :article "a"}
   
   {:name "receipt from a bunny outfit rental"
    :article "a"}
   
   {:name "floppy disk"
    :article "a"}
   
   {:name "pencil"
    :article "a"}
   
   {:name "lantern"
    :article "a"}
   
   {:name "elven sword"
    :article "an"}
   
   {:name "books"
    :article "some"}
   
   {:name "movie ticket"
    :article "a"}
   
   {:name "newspaper"
    :article "a"}
   
   {:name "kitten"
    :article "a"}
   
   {:name "puppy"
    :article "a"}
   
   {:name "bag of potatoes"
    :article "a"}
   
   {:name "bag of rice"
    :article "a"}
   
   {:name "giant styrofoam peanut"
    :article "a"}
   
   {:name "phone book"
    :article "a"}
   
   {:name "pyramid of tennis balls"
    :article "a"}
   
   {:name "deflated soccer ball"
    :article "a"}
   
   {:name "fourth grade report card"
    :article "your"}
   
   {:name "half-eaten sandwich"
    :article "a"}
   
   {:name "signed photograph of Richard Moll"
    :article "a"}
   
   {:name "hipster t-shirt"
    :article "a"}
   
   {:name "pile of discarded puppets"
    :article "a"}
   
   {:name "wet Lincoln Log"
    :article "a"}
   
   {:name "VHS tape covered in blood"
    :article "a"}])

(def animals
  [{:name "kitten"
    :article "a"
    :sounds ["purrs" "meows" "growls"]
    :adjectives ["purring" "meowing" "growling"]}

   {:name "cat"
    :article "a"
    :sounds ["purrs" "meows" "growls"]
    :adjectives ["purring" "meowing" "growling"]}

   {:name "puppy"
    :article "a"
    :sounds ["pants" "barks" "growls" "whimpers"]
    :adjectives ["panting" "barking" "growling" "whimpering"]}

   {:name "duck"
    :article "a"
    :sounds ["quacks"]
    :adjectives ["quacking"]}

   {:name "marmot"
    :article "a"}

   {:name "tiger"
    :article "a"
    :sounds ["roars"]
    :adjectives ["roaring"]}

   {:name "hamster"
    :article "a"}

   {:name "gerbil"
    :article "a"}

   {:name "hedgehog"
    :article "a"}])

(def noises 
  [{:name "foghorn"
    :article "a"}

   {:name "laughter"
    :article "some"}

   {:name "laughing"
    :article "somebody"}

   {:name "chuckling"
    :article "someone"}

   {:name "cackling"
    :article "someone"}

   {:name "crying"
    :article "someone"}

   {:name "sobbing"
    :article "someone"}

   {:name "sneeze"
    :article "a"}

   {:name "wolves howling"}

   {:name "ice cream truck"
    :article "an"}

   {:name "door slam"
    :article "a"}

   {:name "sinister chuckle"
    :article "a"}])

(def disasters
  [{:name "fire"
    :article "a"}

   {:name "tornado"
    :article "a"}

   {:name "hurricane"
    :article "a"}

   {:name "flood"
    :article "a"}

   {:name "tsunami"
    :article "a"}

   {:name "landslide"
    :article "a"}

   {:name "avalanche"
    :article "an"}

   {:name "radioactive leak"
    :article "a"}

   {:name "lava flow"
    :article "a"}

   {:name "sandstorm"
    :article "a"}

   {:name "lightning strike"
    :article "a"}

   {:name "plague of locusts"
    :article "a"}

   {:name "snowstorm"
    :article "a"}

   {:name "duststorm"
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