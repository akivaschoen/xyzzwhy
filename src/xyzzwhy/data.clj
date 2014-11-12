(ns xyzzwhy.data)

(def event-types
  (list
    "location-event"
    "action-event"))

(def location-events
  (list
    "You have entered {{room}}."
    "You are standing {{direction}} of {{room}}."
    "You are in {{room}}."
    "This is {{room}}."
    "You open the secret door only to see {{room}}."
    "You find yourself in {{room}}."
    "You start doing the worm until you find yourself in {{room}}."
    "You wake up from an odd dream unsure of where you are."
    "You wake up in {{room}}."
    "You climb down the tree and find yourself in {{room}}."
    "The taxi driver randomly drops you off at {{room}}."
    "The fog clears and you find yourself in {{room}}."
    "After walking for a long time, you find yourself in {{room}}."
    "You find your way blindly and end up at {{room}}."
    "No matter how hard you tried, you still end up at {{room}}."
    "You climb out of the treasure chest. You are now in {{room}}."
    "You come to in {{room}}."
    "After scrambling through some dense underbrush, you find yourself in {{room}}."))

(def action-events
  (list
    "You awake from a nightmare. You saw yourself in {{room}}. The corpse of {{person}} was there, holding {{item}}."
    "You grab {{item}}, hoping {{person}} doesn't notice."
    "You pick up {{item}}."
    "You drop {{item}}."
    "The radio crackles to life. 'Mayday, mayday, it's {{person}} calling. We're in trouble. We need assistance. Mayday, mayday.'"
    "You pick up {{item}}. Was this here before?"
    "You find {{item}} but decide to leave it alone."
    "{{person}} drops {{item}}, looks at you {{adverb}}, then leaves."
    "Suddenly, {{person}} {{action}} you!"
    "{{person}} {{action}} {{person}}!"
    "{{person}} {{action}} you!"
    "{{person}} drops {{item}} here."
    "{{person}} starts breakdancing and won't stop no matter how much you scream."
    "{{person}} attacks you and knocks you out! You awake sometime later in {{room}}."
    "{{person}} appears in a puff of smoke and shouts, 'You will never go to {{location}} again!'"
    "You startle {{person}} who drops {{item}} and then runs away."
    "{{person}} slams down an empty glass. 'All this nonsense about {{item}} needs to stop!' I can't take it anymore!'"
    "You get tired of waiting for your Uber and decide to walk to {{room}} instead."
    "The phone rings. {{person}} stares at it {{adverb}}. You refuse to answer it. Eventually the phone stops ringing."
    "You start eating {{food}} and don't stop until you're done."
    "You start to eat {{food}} but it doesn't taste very good."
    "You eat {{food}}. {{person}} looks {{adjective}}."
    "You check your inventory. You are carrying {{item}}, {{item}}, and {{item}}."
    "You check your inventory. You are carrying {{item}} and {{item}}."
    "You check your inventory. You are empty-handed."
    "You open up your copy of {{book}}. Someone has scribbled all over the margins. You throw it down on the floor in disgust."
    "{{person}} picks up {{item}}."
    "You start spinning around and around while {{person}} claps and cheers."
    "{{person}} calls from {{room}} asking for {{item}}."
    "You peek out the window. {{person}} is messing around with your mailbox. You crouch in fear."
    "You check your health: you are {{diagnose}}."))

(def secondary-events
  (list
    "You see {{item}} here."
    "There is {{item}} here."
    "{{person}} is here looking {{adjective}}."
    "Something smells {{scent}} here."
    "You hear {{noise}} in the distance."
    "You hear the sound of {{noise}} nearby."
    "You find {{person}}, hiding under a table."
    "{{person}} is here, hiding under a table."
    "You find {{person}}, hiding behind a sofa."
    "{{person}} is here, hiding behind a sofa."
    "{{person}} is here, munching on {{food}}."
    "{{person}} is here, pretending to be invisible."
    "{{person}} is here, having a coughing fit."
    "{{person}} is here, having a sneezing fit."
    "{{person}} is here, being menaced by a hamster."
    "{{person}} is here."
    "{{person}} says, 'I've been waiting for you.'"
    "It appears abandoned."
    "Someone has been here recently."
    "It seems that no one has been here for a long time."
    "Someone has attached marionnette wires to your hands, feet, and head."
    "There has been significant damage from a fire."))

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
    "You wish you had your grandpappy's harmonica."
    "You are starting to feel sleepy."
    "You think about brushing your hair but change your mind."
    "A warm breeze blows by."
    "A cool breeze blows by."
    "You are starting to feel hungry."))

(def rooms
  (list
    "an all-you-can-eat buffet"
    "a grotto"
    "your bedroom"
    "McDonald's"
    "a dark area"
    "a breezy cave"
    "a forest"
    "a riverbed"
    "the AT&T Store"
    "an Apple Store"
    "Whole Foods"
    "a haunted house"
    "a coffin"
    "the trunk of a car"
    "an airplane about to crash"
    "a ballpit"
    "a graveyard"
    "a playground"
    "a pile of diapers"
    "a meeting"
    "a Luby's"))

(def foods
  ; Ex. 'Someone is nearby munching on a burrito.'
  (list
    "a cup of steaming gravy"
    "a burrito"
    "a salad"
    "a Reese's Peanut Butter Cup"
    "an apple pocket"
    "an apple cinnamon Pop Tart"
    "a wedge of cheese"
    "a wedge of cheese with some mold on it"
    "a slice of fried spam"
    "a moist churro"
    "a chocolate bobka"
    "a Cinnabon"
    "some duck confit"
    "some pasta"
    "some uncooked rice"
    "some Fritos"
    "some sushi"
    "some old fruit leather"))

(def books
  (list
    "Catcher in the Rye"
    "Infinite Jest"
    "Gravity's Rainbow"
    "A Prayer for Owen Meany"))

(def -items
  (list
    "some vitamins"
    "a bucket of corks"
    "a pair of jean shorts"
    "a non-Euclidian Lego"
    "spray-on bacon"
    "spackle"
    "an unfamiliar briefcase"
    "a towel from the Las Vegas Radisson"
    "a receipt for a bunny outfit"
    "a floppy disk"
    "a pencil"
    "a lantern"
    "an elven sword"
    "some books"
    "a movie ticket"
    "a newspaper"
    "a kitten"
    "a puppy"
    "a bag of potatoes"
    "a bag of rice"
    "a giant styrofoam peanut"
    "a phone book"
    "a pyramid of tennis balls"
    "a deflated soccer ball"
    "your fourth grade report card"
    "a half-eaten sandwich"
    "a signed photograph of Richard Moll"
    "a hipster t-shirt"
    "a pile of discarded puppets"
    "a wet Lincoln Log"
    "a VHS tape covered in blood"))

(def items
  (concat food (map #(str "a copy of " %) books) -items))

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

(def monsters
  (list
    "orc"
    "ogre"
    "troll"))

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

(def actors
  (concat persons monsters))

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

(def noises
  ; Ex. 'You hear a foghorn in the distance.'
  (list
    "a foghorn"
    "laughter"
    "crying"
    "someone crying"
    "someone sneeze"
    "a sneeze"
    "wolves howling"
    "an ice cream truck"
    "a door slam"
    "a sinister chuckle"))

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

