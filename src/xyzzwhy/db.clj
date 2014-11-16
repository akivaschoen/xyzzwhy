(ns xyzzwhy.db
  (:require [clojure.string :as string]
            [monger.core :refer [get-db connect]]
            [monger.collection :refer [insert-batch]]
            [xyzzwhy.data :refer [db]]))

(defn- encode-collection-name [s] (string/replace s #"-" "_"))

(def event-types
  [{:name "location-event"}
   {:name "action-event"}])

(def location-events
  [{:name "You have entered {{room}}."}
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
   {:name "The trapdoor drops open underneath you and you land {{room-with-prep}}."}
   {:name "You get tangled up in a revolving door. You stumble out into {{room}}."}
   {:name "After scrambling through some dense underbrush, you find yourself {{room-with-prep}}."}])

(def action-events
  [{:name "You awake from a nightmare. You saw yourself {{room-with-prep}}. The corpse of {{person}} was there, holding {{item}}."}
   {:name "You grab {{item}}, hoping {{person}} doesn't notice."}
   {:name "You pick up {{item}}."}
   {:name "You drop {{item}}."}
   {:name "The radio crackles to life. 'Mayday, mayday, it's {{person}} calling. We're in trouble. We need assistance. Mayday, mayday.'"}
   {:name "You pick up {{item}}. Was this here before?"}
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
   {:name "{{person}} slams down an empty glass. 'All this nonsense about {{item}} needs to stop! I can't take it anymore!'"}
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
   {:name "You check your inventory. You are have {{item}} and {{item}}."}
   {:name "You open up your copy of {{book}}. Someone has scribbled all over the margins. You throw it down on the floor in disgust."}
   {:name "{{actor}} picks up {{item}}."}
   {:name "You start spinning around and around while {{person}} claps and cheers."}
   {:name "{{person}} is calling from {{room}} asking for {{item}}."}
   {:name "You peek out the window. {{person}} is messing around with your mailbox. You crouch in fear."}
   {:name "In the distance, you hear {{person}} let the bass drop."}
   {:name "You check your health: you are {{diagnose}}."}])

(def secondary-events 
  [{:name "You see {{item}} here."}
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
   {:name "It seems that no one has been here for a long time."}
   {:name "Someone has attached marionnette wires to your hands, feet, and head."}
   {:name "Someone has left a running bulldozer here."}
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
   {:name "You wish you had your grandpappy's harmonica."}
   {:name "You are starting to feel sleepy."}
   {:name "You think about brushing your hair but change your mind."}
   {:name "You have no idea how these rope burns got on your wrists."}
   {:name "You feel as if you're being followed."}
   {:name "A warm breeze blows by."}
   {:name "A cool breeze blows by."}
   {:name "A basketball bounces by."}
   {:name "You spot a balloon stuck in a tree."}
   {:name "Somehow, you've lost your {{garment}}."}
   {:name "You hear someone breaking eggs and sobbing nearby."}
   {:name "You hear someone whisking heavy cream while laughing nearby."}
   {:name "You are starting to feel hungry."}])

(def actor-actions
  [{:name " looking {{adjective}}."}
   {:name ", hiding under a table."}
   {:name ", hiding under a sofa."}
   {:name ", munching on {{food}}."}
   {:name ", pretending to be invisible."}
   {:name ", having a coughing fit."}
   {:name ", having a sneezing fit."}
   {:name ", being menaced by {{animal}}."}
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
  [{:name " says, 'I've been waiting for you.'"}
   {:name " says, 'I can't find my {{garment}}.'"}
   {:name " asks, 'Why am I holding this pitchfork?'"}
   {:name " asks, 'Does it smell like {{food}} in here to you?'"}])

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
  [{:name "Samuel L. Jackson"}
   {:name "Johnny Cash"}
   {:name "a police officer"}
   {:name "Alex Trebek"}
   {:name "Craig Ferguson"}
   {:name "Geoff Petersen"}
   {:name "Stephen King"}
   {:name "Gene Shalit"}
   {:name "Clive Chatterjee"}
   {:name "Chris Morgan"}
   {:name "Nancy Grace"}
   {:name "Lindsay Lohan"}
   {:name "Barack Obama"}
   {:name "Abe Vigoda"}
   {:name "Louis Gray"}
   {:name "Brad Pitt"}
   {:name "Bill Maher"}
   {:name "Grace Jones"}
   {:name "George W. Bush"}
   {:name "your mom"}
   {:name "a bunch of kids"}
   {:name "a crowd of Yoga enthusiasts"}
   {:name "George Clooney"}
   {:name "James Franco"}
   {:name "Jonah Hill"}
   {:name "Scarlet Johannson"}
   {:name "a gas station attendant"}
   {:name "Zombie Carl Sagan"}])

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
   {:name "forlorn"}
   {:name "angry"}])

(def adverbs
  [{:name "carefully"}
   {:name "wistfully"}
   {:name "lustfully"}
   {:name "warily"}
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

(defn add-database-collection [name]
  (insert-batch db (encode-collection-name name) @(-> name symbol resolve)))

(defn add-all-collections []
  (doseq [collection ["event-types"
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
                      "disasters"]]
    (add-database-collection collection)))
