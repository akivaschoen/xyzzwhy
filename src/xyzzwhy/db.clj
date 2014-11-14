(ns xyzzwhy.db)

(def event-types
  [{:type "location-event"}
   {:type "action-event"}])

(def rooms
  [{:name "tire fire" 
    :article "a" 
    :long_name "{{adverb}} burning tire fire"
    :preps ["at" "near" "behind" "in front of"] }
   
   {:name "dildo bonfire" 
    :article "a" 
    :long_name "{{adverb}} burning dildo bonfire"
    :preps ["at" "near" "behind" "in front of"] }

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
    :article "a"
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

(def location-events
  (list
    "You have entered {{room}}."
    "You are standing {{direction}} of {{room}}."
    "You stumble into {{room}}."
    "You come across {{room}}."
    "You are {{room-with-prep}}."
    "This is {{room}}."
    "You open the secret door only to see {{room}}."
    "You find yourself {{room-with-prep}}."
    "You start doing the worm until you find yourself {{room-with-prep}}."
    "You wake up from an odd dream unsure of where you are."
    "You wake up {{room-with-prep}}."
    "You climb down the tree and find yourself {{room-with-prep}}."
    "The taxi driver randomly drops you off {{room-with-prep}}."
    "The fog clears and you find yourself {{room-with-prep}}."
    "After walking for a long time, you find yourself {{room-with-prep}}."
    "You find your way blindly and end up {{room-with-prep}}."
    "No matter how hard you try, you still end up {{room-with-prep}}."
    "You climb out of the treasure chest. You are now {{room-with-prep}}."
    "You come to {{room-with-prep}}."
    "You follow the winding path only to find yourself {{room-with-prep}}."
    "The elevator doors open to reveal {{room}}."
    "The trapdoor drops open underneath you and you land {{room-with-prep}}."
    "You get tangled up in a revolving door. You stumble out into {{room}}."
    "After scrambling through some dense underbrush, you find yourself {{room-with-prep}}."))

(def action-events
  (list
    "You awake from a nightmare. You saw yourself {{room-with-prep}}. The corpse of {{person}} was there, holding {{item}}."
    "You grab {{item}}, hoping {{person}} doesn't notice."
    "You pick up {{item}}."
    "You drop {{item}}."
    "The radio crackles to life. 'Mayday, mayday, it's {{person}} calling. We're in trouble. We need assistance. Mayday, mayday.'"
    "You pick up {{item}}. Was this here before?"
    "You find {{item}} but decide to leave it alone."
    "{{actor}} drops {{item}}, looks at you {{adverb}}, then leaves."
    "Suddenly, {{actor}} {{action}} you!"
    "{{actor}} {{action}} {{actor}}!"
    "{{actor}} {{action}} you!"
    "{{actor}} drops {{item}} here."
    "{{person}} starts breakdancing and won't stop no matter how much you scream."
    "{{actor}} attacks you and knocks you out! You awake sometime later in {{room}}."
    "{{person}} appears in a puff of smoke and shouts, 'You will never go to {{room}} again!'"
    "You startle {{person}} who drops {{item}} and then runs away."
    "{{person}} slams down an empty glass. 'All this nonsense about {{item}} needs to stop! I can't take it anymore!'"
    "{{person}} suddenly shrieks."
    "{{person}} shouts, 'You can't go up against city hall!'"
    "You get tired of waiting for your Uber and decide to walk to {{room}} instead."
    "The phone rings. {{person}} stares at it {{adverb}}. You refuse to answer it. Eventually the phone stops ringing."
    "You start eating {{food}} and don't stop until you're done."
    "You start to eat {{food}} but it doesn't taste very good."
    "You eat {{food}}. {{actor}} looks on {{adverb}}."
    "You check your inventory. You are carrying {{item}}, {{item}}, and {{item}}."
    "You check your inventory. You are carrying {{item}} and {{item}}."
    "You check your inventory. You are empty-handed."
    "You open up your copy of {{book}}. Someone has scribbled all over the margins. You throw it down on the floor in disgust."
    "{{actor}} picks up {{item}}."
    "You start spinning around and around while {{person}} claps and cheers."
    "{{person}} is calling from {{room}} asking for {{item}}."
    "You peek out the window. {{person}} is messing around with your mailbox. You crouch in fear."
    "{{person}} says, 'I can't find my protractor.'"
    "{{person}} asks, 'Why am I holding this pitchfork?'"
    "In the distance, you hear {{person}} let the bass drop."
    "You check your health: you are {{diagnose}}."))

(def secondary-events
  (list
    "You see {{item}} here."
    "There is {{item}} here."
    "{{actor}} is here."
    "{{actor}} is here{{actor-action}}"
    "You find {{actor}}{{actor-action}}"
    "{{person}} {{dialogue}}"
    "{{actor}} is here searching for {{item}}."
    "{{actor}} is here hoping to run into {{actor}}."
    "Something smells {{scent}} here."
    "You hear {{noise}} in the distance."
    "You hear the sound of {{noise}} nearby."
    "The wind howls in the distance."
    "It appears abandoned."
    "Someone has been here recently."
    "It seems that no one has been here for a long time."
    "Someone has attached marionnette wires to your hands, feet, and head."
    "Someone has left a running bulldozer here."
    "There has been significant damage from {{disaster}}."))

(def actor-actions
  (list
    " looking {{adjective}}."
    ", hiding under a table."
    ", hiding under a sofa."
    ", munching on {{food}}."
    ", pretending to be invisible."
    ", having a coughing fit."
    ", having a sneezing fit."
    ", being menaced by {{animal}}."))

(def dialogues
  (list
    "says, 'I've been waiting for you.'"
    "asks, 'Does it smell like {{food}} in here to you?'"))

(def tertiary-events
  (list
    "You aren't wearing any clothes."
    "Your shoes are on the wrong feet."
    "Your tie feels uneven."
    "You're not wearing any underwear."
    "You do a little jig and then whistle."
    "You clap once."
    "You have socks on your hands."
    "You feel nervous."
    "You feel anxious."
    "You feel cold."
    "You feel warm."
    "You blink really slowly."
    "You yawn."
    "You wish you had your grandpappy's harmonica."
    "You are starting to feel sleepy."
    "You think about brushing your hair but change your mind."
    "You have no idea how these rope burns got on your wrists."
    "You feel as if you're being followed."
    "A warm breeze blows by."
    "A cool breeze blows by."
    "A basketball bounces by."
    "You spot a balloon stuck in a tree."
    "Somehow, you've lost your {{garment}}."
    "You are starting to feel hungry."))

(def books
  (list
    "the Bible"
    "Catcher in the Rye"
    "Infinite Jest"
    "Gravity's Rainbow"
    "A Prayer for Owen Meany"))

(def directions
  (list
    "north"
    "northeast"
    "east"
    "southeast"
    "south"
    "southwest"
    "west"
    "northwest"))

(def persons
  (list
    "Samuel L. Jackson"
    "Johnny Cash"
    "a police officer"
    "Alex Trebek"
    "Craig Ferguson"
    "Geoff Petersen"
    "Stephen King"
    "Gene Shalit"
    "Clive Chatterjee"
    "Chris Morgan"
    "Nancy Grace"
    "Lindsay Lohan"
    "Barack Obama"
    "Abe Vigoda"
    "Louis Gray"
    "Brad Pitt"
    "Bill Maher"
    "Grace Jones"
    "George W. Bush"
    "your mom"
    "a bunch of kids"
    "a crowd of Yoga enthusiasts"
    "George Clooney"
    "James Franco"
    "Jonah Hill"
    "Scarlet Johannson"
    "a gas station attendant"
    "Zombie Carl Sagan"))

(def actions
  ; Ex. 'Suddenly, Lindsay Lohan tickles you!'
  (list
    "attacks"
    "ignores"
    "tickles"
    "stands uncomfortably close to"
    "pets"
    "flirts with"))

(def adjectives
  (list
    "worried"
    "relieved"
    "aroused"
    "afraid"
    "sleepy"
    "hungry"
    "thirsty"
    "bored"
    "hopeful"
    "sad"
    "forlorn"
    "angry"))

(def adverbs
  (list
    "carefully"
    "wistfully"
    "lustfully"
    "warily"
    "balefully"))

(def scents
  (list
    "acrid"
    "sweet"
    "sour"
    "rotten"
    "nice"
    "foul"
    "like feet"
    "like your grandfather's hair cream"
    "bitter"
    "smoky"
    "gross"
    "pleasant"))

(def diagnoses
  ; Ex. 'You are feeling great.'
  (list
    "feeling great"
    "lightly wounded"
    "moderately wounded"
    "heavily wounded"
    "near death"
    "sleepy"
    "drunk"
    "stoned"
    "confused"
    "hungry"
    "thirsty"
    "temporarily blind"
    "temporarily deaf"
    "covered in bees"))

(def foods
  ; Ex. 'Someone is nearby munching on a burrito.'
  [{:name "burrito"
    :article "a"}
   
   {:name "salad"
    :article "a"}

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
    :article "a"}])

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
   
   {:name "receipt for a bunny outfit rental"
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
