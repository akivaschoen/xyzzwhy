(ns xyzzwhy.text)

;; This text presents source material for xyzzwhy's original corpus. Each
;; class (such as :location-events or :obstacles) is a map containing map
;; fragments the minimal requirement of which is a :text key.
;;
;; A fragment's :text value can contain a number of substitutions represented
;; by %0-%n. These require a :subs map which contains a key for each substitution.
;; These must contain at least a :class reference which is keyed to another
;; class. These are randomly chosen by xyzzwhy and then interpolated into the
;; fragment's :text. A substitution can also contain a :config map which has
;; options to skip the automatic prepending of prepositions and/or articles; and
;; the ability to ensure that an actor sub is a single person and not a group.
;; More config options will be added later.
;;
;; A fragment may also have follow-ups which may be marked as :optional?. A list of
;; options follows which is a vector of fragments which themselves may have :subs.
;; These subs must begin their %x count +1 from what's in the parent fragment's :text.
;; Each follow-up option's sub may contain a :ref key which allows it to refer to
;; a substitution from the parent fragment's text. For example, to use a pronoun
;; for a person class substitution at %0, the follow-up's substitution would :ref 0.
;;
;; Object classes and locations also have an :article and :preps for prepositions.
;; Locations have a :type which is either :interior or :exterior.
;; Actors have a :gender (:male, :female, or :group).
;;
;; Animals have :sounds (e.g., a kitten might purr or meow) and :adjectives
;; such as a 'whimpering' for a puppy.
;;
;; Future classes might have their own special keys and options.

(def events
  {:events [:location-event :action-event]})

(def location-events
  {:location-events
   [{:text "You have entered %0."
     :subs {0 {:class :location
               :config #{:no-prep}}}}

    {:text "You are serving jury duty."
     :follow-ups {:optional? false
                  :options [{:text "The prosecuting attorney insists on speaking through a broken bullhorn."}
                            {:text "They hand out Chicken McNuggets but they're stone cold."}
                            {:text "The judge is dressed like Dr. Frankenfurter."}
                            {:text "You're unsure why they insist the jury sit in a vat of carbonated %0."
                             :subs {0 {:class :drink :config #{:no-article}}}}]}}

    {:text "After %0 at your %1, you are relocated by FEMA to %2."
     :subs {0 {:class :disaster}
            1 {:class :location :config #{:no-prep :no-article}}
            2 {:class :location :config #{:no-prep}}}}

    {:text "You arrive for your first day at college only to find your roommate is %0."
     :subs {0 {:class :actor}}}

    {:text "You try to go %0 but your way is blocked by %1."
     :subs {0 {:class :direction}
            1 {:class :obstacle}}}

    {:text "%0 prevents you from going %1 to %2."
     :subs {0 {:class :obstacle}
            1 {:class :direction}
            2 {:class :location :config #{:no-prep}}}}

    {:text "You go %0 and find yourself at %1."
     :subs {0 {:class :direction}
            1 {:class :location :config #{:no-prep}}}}

    {:text "You go %0 and emerge %1."
     :subs {0 {:class :direction}
            1 {:class :location}}}

    {:text "You walk %0 and arrive at %1."
     :subs {0 {:class :direction}
            1 {:class :location :config #{:no-prep}}}}

    {:text "You cripwalk %0 to %1."
     :subs {0 {:class :direction}
            1 {:class :location}}}

    {:text "You strut %0 and end up %1."
     :subs {0 {:class :direction}
            1 {:class :location}}}

    {:text "A double bounce on a trampoline lands you %0."
     :subs {0 {:class :location}}}

    {:text "You head %0 and arrive at %1."
     :subs {0 {:class :direction}
            1 {:class :location :config #{:no-prep}}}}

    {:text "Google Maps leads you to %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You follow %0 to %1."
     :subs {0 {:class :actor}
            1 {:class :location :config #{:no-prep}}}}

    {:text "You are %0."
     :subs {0 {:class :location}}}

    {:text "You're %0."
     :subs {0 {:class :location}}}

    {:text "You run screaming into %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You tunnel through the soil and pop up %0."
     :subs {0 {:class :location}}}

    {:text "You emerge %0."
     :subs {0 {:class :location}}}

    {:text "You arrive %0."
     :subs {0 {:class :location}}}

    {:text "You are magically teleported to %0!"
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "The drugs are wearing off. You are %0."
     :subs {0 {:class :location}}}

    {:text "The spell effects are wearing off. You are %0."
     :subs {0 {:class :location}}}

    {:text "You are standing %0 of %1."
     :subs {0 {:class :direction}
            1 {:class :location :config #{:no-prep}}}}

    {:text "You stumble into %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You come across %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You follow the treasure map to %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You wake up from an odd dream. You are %0."
     :subs {0 {:class :location}}}

    {:text "You open the secret door only to see %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You find yourself %0."
     :subs {0 {:class :location}}}

    {:text "Dazed, you climb out of the dryer. You are %0."
     :subs {0 {:class :location}}}

    {:text "After the shoot-out, you make your escape to %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "The bridge game turned violent so you went to %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You start doing the worm until you find yourself %0."
     :subs {0 {:class :location}}}

    {:text "You wake up %0."
     :subs {0 {:class :location}}}

    {:text "You climb down a tree and find yourself %0."
     :subs {0 {:class :location}}}

    {:text "You climb up a tree and find yourself %0."
     :subs {0 {:class :location}}}

    {:text "The taxi driver randomly drops you off %0."
     :subs {0 {:class :location}}}

    {:text "The metro bus unceremoniously dumps you %0."
     :subs {0 {:class :location}}}

    {:text "The fog clears and you find yourself %0."
     :subs {0 {:class :location}}}

    {:text "Your parachute gently plops you %0."
     :subs {0 {:class :location}}}

    {:text "You jump out of a moving car, roll down a hill, and find yourself %0."
     :subs {0 {:class :location}}}

    {:text "After walking for a long time, you find yourself %0."
     :subs {0 {:class :location}}}

    {:text "You find your way blindly and end up %0."
     :subs {0 {:class :location}}}

    {:text "No matter how hard you try, you still end up %0."
     :subs {0 {:class :location}}}

    {:text "You climb out of the treasure chest. You are now %0."
     :subs {0 {:class :location}}}

    {:text "You come to %0."
     :subs {0 {:class :location}}}

    {:text "You follow a winding path %0 only to find yourself %1."
     :subs {0 {:class :direction}
            1 {:class :location}}}

    {:text "You follow a sloping path %0. You find yourself %1."
     :subs {0 {:class :direction}
            1 {:class :location}}}

    {:text "You climb up a flight of stairs. You are now %0."
     :subs {0 {:class :location}}}

    {:text "You shuffle down a flight of stairs and enter %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You fall down a flight of stairs and into %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "The elevator doors open to reveal %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "Using a vine to swing across the pit, you land %0."
     :subs {0 {:class :location}}}

    {:text "The trapdoor drops open beneath you and you land %0."
     :subs {0 {:class :location}}}

    {:text "You flip the Game Select selector and find yourself %0."
     :subs {0 {:class :location}}}

    {:text "You blow into the cartridge too hard and are teleported to %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "You step through a magic mirror and end up %0."
     :subs {0 {:class :location}}}

    {:text "You get tangled up in a revolving door. You stumble out into %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "After scrambling through some dense underbrush, you find yourself %0."
     :subs {0 {:class :location}}}

    {:text "After pushing your way through a dense crowd, you arrive %0."
     :subs {0 {:class :location}}}

    {:text "You squeeze out of the sewage outflow and tumble into %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "A tornado deposits you %0."
     :subs {0 {:class :location}}}

    {:text "Right on cue, you pop up out of the jack-in-the-box. You're %0."
     :subs {0 {:class :location}}}

    {:text "After being shot out of a cannon, you land %0."
     :subs {0 {:class :location}}}

    {:text "You slide down a fireman's pole and land %0."
     :subs {0 {:class :location}}}

    {:text "Hands on your hips, you survey %0 %1."
     :subs {0 {:class :location :config #{:no-prep}}
            1 {:class :adverb}}}

    {:text "You wake up in front of %0's house. You have no clue how you got there."
     :subs {0 {:class :person}}}

    {:text "You ride %0 to %1."
     :subs {0 {:class :animal}
            1 {:class :location :config #{:no-prep}}}}

    {:text "You fall through the ceiling and land %0."
     :subs {0 {:class :location}}}

    {:text "The drugs are starting to take hold. Casually you saunter over to %0."
     :subs {0 {:class :location :config #{:no-prep}}}}

    {:text "The merry-go-round spins faster and faster until you're flung off and land %0."
     :subs {0 {:class :location}}}]})


(def action-events
  {:action-events
   [{:text "You awake from a nightmare. You saw yourself %0. The corpse of %1 was there, holding %2."
     :subs {0 {:class :location}
            1 {:class :person :config #{:no-groups}}
            2 {:class :item}}}

    {:text "%0 arrives from the %1, carrying %2."
     :subs {0 {:class :actor :config #{:no-groups}}
            1 {:class :direction}
            2 {:class :item}}}

    {:text "You pick up %0."
     :subs {0 {:class :item}}}

    {:text "You pick up %0 and hold it close to your chest."
     :subs {0 {:class :item}}
     :follow-ups {:optional? false
                  :options [{:text "hold it close to your chest."}
                            {:text "hold it up to %1's face."
                             :subs {0 {:class :person}}}]}}

    {:text "The radio crackles to life."
     :follow-ups {:optional? false
                  :options [{:text "It sounds like someone with a cold is eating Rice Krispies." }
                            {:text "A hollow voice intones, '%0'"
                             :subs {0 {:class :intonation}}}
                            {:text "Ketchup begins seeping through the speaker holes."}
                            {:text "It continues to crackle to life. It's still crackling. It's on fire."}
                            {:text "An announcer shouts, 'They found rice on Mars!'"}
                            {:text "A news report is on about %0 %1."
                             :subs {0 {:class :disaster}
                                    1 {:class :location :config #{:no-prep}}}}
                            {:text "%0 solemnly '%1'"}]}}

    {:text "%0 drops %1, looks at you %2, then leaves."
     :subs {0 {:class :actor}
            1 {:class :item}
            2 {:class :adverb}}}

    {:text "You check your messages."
     :follow-ups {:optional? false
                  :options [{:text "%0 %1"
                             :subs {0 {:class :person :config #{:no-groups}}
                                    1 {:class :dialogue}}}]}}

    {:text "You read morning paper."
     :follow-ups {:optional? false
                  :options {:text "%0"
                            :subs {0 {:class :news}}}}}

    {:text "%0 gently places %1 on the ground and then backs away slowly."
     :subs {0 {:class :actor}
            1 {:class :item}}}

    {:text "%0 %1 %2."
     :subs {0 {:class :actor}
            1 {:class :action}
            2 {:class :actor}}}

    {:text "%0 %1 you."
     :subs {0 {:class :actor}
            1 {:class :action}}}

    {:text "%0 drops %1 here."
     :subs {0 {:class :actor}
            1 {:class :item}}}

    {:text "%0 does a little jig. 'Bidibidibidi, wanna dance?'"
     :subs {0 {:class :actor}}}

    {:text "%0 marches up to you and says, 'Hello please.'"
     :subs {0 {:class :person}}}

    {:text "%0 starts breakdancing and won't stop no matter how much you scream."
     :subs {0 {:class :person}}}

    {:text "%0 attacks you and knocks you out. You awake sometime later %1."
     :subs {0 {:class :actor}
            1 {:class :location}}}

    {:text "%0 attacks you but you fight back with %1,"
     :subs {0 {:class :actor}
            1 {:class :attack}}
     :follow-ups {:optional? false
                  :options [{:text "winning the battle."}
                            {:text "taking only a hit to your pride."}]}}

    {:text "%0 attacks you but you fight back with %1."
     :subs {0 {:class :actor}
            1 {:class :attack}}
     :follow-ups {:optional? false
                  :options [{:text "It isn't enough: you lose."}
                            {:text "This scares the shit out of %2. %3 runs away."
                             :subs {2 {:ref 0
                                       :class :gender
                                       :case :objective}
                                    3 {:ref 0
                                       :class :gender
                                       :case :subjective}}}]}}

    {:text "%0 attacks with %1. You execute %2."
     :subs {0 {:class :actor}
            1 {:class :attack}
            2 {:class :attack}}
     :follow-ups {:optional? false
                  :options [{:text "You are victorious!"}
                            {:text "You have been killed."}
                            {:text "You have been slain!"}
                            {:text "%0 keels over all dead and stuff."
                             :subs {0 {:class :gender
                                       :case :subjective}}}]}}

    {:text "%0 attacks with %1. You respond with %2. You are defeated."
     :subs {0 {:class :actor}
            1 {:class :attack}
            2 {:class :attack}}}

    {:text "%0 attacks with %1. You strike back with %2. FATALITY. But who?!"
     :subs {0 {:class :actor}
            1 {:class :attack}
            2 {:class :attack}}}

    {:text "%0 attacks with %1. You with %2."
     :subs {0 {:class :actor}
            1 {:class :attack}
            2 {:class :attack}}
     :follow-ups {:optional? false
                  :options [{:text "Look at you what with the killing and all."}
                            {:text "%0 is a bloodstain."
                             :subs {0 {:class :gender
                                       :case :subjective}}}]}}

    {:text "Suddenly you're in freeze-frame as the credits roll."}

    {:text "%0 appears in a puff of smoke, %1"
     :subs {0 {:class :person}
            1 {:class :actor-action}}}

    {:text "You startle %0 who drops %1 and runs away."
     :subs {0 {:class :person}
            1 {:class :item}}}

    {:text "%0 slams down a half-empty glass of %1."
     :subs {0 {:class :person}
            1 {:class :drink :config #{:no-prep :no-article}}}
     :follow-ups {:optional? false
                  :options [{:text "'All this nonsense about %0 needs to stop! I can't take it anymore!'"
                             :subs {0 {:class :item}}}
                            {:text "'They're making plans for Nigel! They want what's best for him!'"}
                            {:text "'You can't go up against city hall!'"}
                            {:text "'I just can't take you seriously anymore!'"}
                            {:text "'MOM?!'"}]}}

    {:text "%0 suddenly shrieks."
     :subs {0 {:class :person}}}

    {:text "%0 makes a rude noise, points surreptitiously to %1 nearby."
     :subs {0 {:class :person}
            1 {:class :animal}}}

    {:text "You get tired of waiting for";
     :follow-ups {:optional? false
                  :options [{:text "your Uber and decide to walk to %0 instead."
                             :subs {0 {:class :location :config #{:no-prep}}}}
                            {:text "your private jet so you decide to walk to %0 instead."
                             :subs {0 {:class :location :config #{:no-prep}}}}
                            {:text "the all-you-can-eat-buffet to open so you walk to %0 instead."
                             :subs {0 {:class :location :config #{:no-prep}}}}]}}

    {:text "The phone rings."
     :follow-ups {:optional? false
                  :options [{:text "%0 stares at it %1. Eventually the ringing stops."
                             :subs {0 {:class :person}
                                    1 {:class :adverb}}}
                            {:text "%0 watches as it starts to melt, the sound of the ring slowing and burbling to a stop."
                             :subs {0 {:class :person}}}
                            {:text "%0 picks it up, listens a moment, shrieks, and slams the phone down again."
                             :subs {0 {:class :person}}}
                            {:text "%0 picks it up, says, 'It's for you,' but you're longer there."
                             :subs {0 {:class :person}}}]}}

    {:text "You start eating %0 and don't stop until you're done."
     :subs {0 {:class :food}}}

    {:text "You eat %0."
     :subs {0 {:class :food}}
     :follow-ups {:optional? true
                  :options [{:text "%0 looks on %1."
                             :subs {0 {:class :actor}
                                    1 {:class :adverb}}}]}}

    {:text "You think to yourself, '%0'"
     :subs {0 {:class :thought}}}

    {:text "You pause and think, '%0'"
     :subs {0 {:class :thought}}}

    {:text "You feel a little famished so you eat %0."
     :subs {0 {:class :food}}}

    {:text "You take a sip of %0."
     :subs {0 {:class :drink :config #{:no-article}}}}

    {:text "You check your inventory."
     :follow-ups {:optional? true
                  :options [{:text "You are empty-handed."}
                            {:text "You are carrying %0, %1, and %2."
                             :subs {0 {:class :item}
                                    1 {:class :item}
                                    2 {:class :item}}}
                            {:text "You have %0 and %1."
                             :subs {0 {:class :item}
                                    1 {:class :item}}}]}}

    {:text "You open up %0."
     :subs {0 {:class :book}}
     :follow-ups {:optional? false
                  :options [{:text "Someone has scribbled all over the margins. You throw it down on the floor in disgust."}
                            {:text "Someone has left a recipe for beef stew inside."}
                            {:text "You read a bit before tossing it over your shoulder and then doing the electric slide."}]}}

    {:text "%0 suddenly appears out of the shadows and"
     :subs {0 {:class :actor}}
     :follow-ups {:optional? false
                  :options [{:text "hisses at you, then scrambles away like a spider."}
                            {:text "says, 'Oh, sorry about that,' then retreats back into the shadows."}
                            {:text "says, '%0 will see you now,' then slowly retreats back into the shadows."
                             :subs {0 {:class :actor}}}]}}

    {:text "%0 picks up %1."
     :subs {0 {:class :actor}
            1 {:class :item}}}

    {:text "An overhead loudspeaker crackles to life,"
     :follow-ups {:optional? false
                  :options [{:text "'Citizen! Report immediately to the nearest oddly-angled inner tube.'"}
                            {:text "'Citizen! Report immediately to the nearest self-incrimination booth.'"}
                            {:text "'Citizen! Report immediately to the nearest Java stacktrace.'"}
                            {:text "'Citizen! Report immediately to the bean simulator.'"}
                            {:text "'Citizen! Report immediately to the nearest certified manhole.'"}
                            {:text "'Citizen! Report immediately to the National Baby Oil Slip-n-Slide.'"}
                            {:text "'Citizen! Report immediately to the Hall of Uncomfortable Touching.'"}
                            {:text "'Citizen! Report immediately to the Bakery of Unravelled Cinnamon Buns.'"}
                            {:text "'Citizen! Stop that.'"}
                            {:text "'Citizen! Report immediately to the Readers' Digest Condensation Camp.'"}
                            {:text "'Citizen! Open up your textbook and turn to the chapter concerning your death.'"}
                            {:text "'Citizen! Report immediately to the Out-of-Control Rototiller Museum.'"}
                            {:text "'Citizen! Report immediately to the nearest mandatory prison hug.'"}
                            {:text "'Citizen! Report immediately to the nearest sanctioned dogpile.'"}
                            {:text "'Citizen! Report immediately to the nearest full-contact Bible study group.'"}
                            {:text "'Citizen! Report immediately to the mannequin factory.'"}
                            {:text "'Citizen! Report immediately to The Garbagerie.'"}
                            {:text "'Citizen! Report immediately to Stall #3.'"}
                            {:text "'Citizen! Just shut up already.'"}]}}

    {:text "An overhead loudspeaker crackles to life. The announcement is completely garbled. The loudspeaker switches off with a squawk."}

    {:text "You start spinning around and around."
     :follow-ups {:optional? false
                  :options [{:text "%0 looks unimpressed."
                             :subs {0 {:class :person}}}
                            {:text "%0 faints."
                             :subs {0 {:class :person}}}
                            {:text "You drill straight into the crust of the earth."}
                            {:text "You gracefully lift off into a blue sky."}
                            {:text "You gracefully lift off into a blue sky never to be seen again."}
                            {:text "You gracefully lift off, go sideways, and crash into a building."}]}}

    {:text "You start spinning around and around while"
     :follow-ups {:optional? false
                  :options [{:text "%0 claps and cheers."
                             :subs {0 {:class :person}}}
                            {:text "%0 cries and points."
                             :subs {0 {:class :person}}}
                            {:text "%0 writes furiously on a clipboard."
                             :subs {0 {:class :person}}}
                            {:text "%0 beams with pride."
                             :subs {0 {:class :person}}}]}}

    {:text "%0 is calling from %1 asking for %2."
     :subs {0 {:class :person}
            1 {:class :location :config #{:no-prep}}
            2 {:class :item}}}

    {:text "%0 is calling from %1"
     :subs {0 {:class :person}
            1 {:class :location :config #{:no-prep}}}
     :follow-ups {:optional? false
                  :options [{:text "asking if %2 can come out and play."
                             :subs {2 {:class :person}}}]}}

    {:text "You peek out the window."
     :follow-ups {:optional? true
                  :options [{:text "%0 is messing around with your mailbox. You crouch in fear."
                             :subs {0 {:class :person}}}
                            {:text "%0 is laying facedown in your flowerbed. You sink to your knees with worry."
                             :subs {0 {:class :person}}}
                            {:text "%0 has set up a lemonade stand in your yard. It's on fire."
                             :subs {0 {:class :person}}}
                            {:text "%0 has set up a lemonade stand in your yard. It's covered in bees."
                             :subs {0 {:class :person}}}
                            {:text "%0 has set up a lemonade stand in your yard. The line stretches around the block."
                             :subs {0 {:class :person}}}
                            {:text "%0 has set up a lemonade stand across the street. You feel oddly jealous."
                             :subs {0 {:class :person}}}
                            {:text "%0 is struggling to start a chainsaw while staring at you. You bite your knuckle."
                             :subs {0 {:class :person}}}
                            {:text "%0 is standing in your yard, painting a portrait of you peeking out the window."
                             :subs {0 {:class :person}}}
                            {:text "Your entire house has been encased in a giant stone column."}]}}

    {:text "In the distance,"
     :follow-ups {:optional? false
                  :options [{:text "you hear %0 let the bass drop."
                             :subs {0 {:class :person}}}
                            {:text "you hear %0 drop the mic."
                             :subs {0 {:class :person}}}
                            {:text "you hear %0 get wicked."
                             :subs {0 {:class :person}}}
                            {:text "you hear %0 shake it off."
                             :subs {0 {:class :person}}}]}}

    {:text "A magician saws you in half... lengthwise."}

    {:text "You check your health: you are %0."
     :subs {0 {:class :diagnose}}}

    {:text "You find yourself being slowly teleported away. Very slowly. People are beginning to stare."}]})

(def secondary-events
  {:secondary-events
   [{:text "You see %0 here."
     :subs {0 {:class :item}}}

    {:text "You see %0 here. It looks oddly familiar."
     :subs {0 {:class :item}}}

    {:text "There is %0 here."
     :subs {0 {:class :item}}}

    {:text "You pick up %0. It's covered in dust."
     :subs {0 {:class :item}}}

    {:text "You pick up %0."
     :subs {0 {:class :item}}}

    {:text "You drop %0 here."
     :subs {0 {:class :item}}
     :follow-ups {:optional? true
                  :options [{:text "It bursts into flames."}
                            {:text "It turns into a wisp of smoke."}
                            {:text "And then some really bad CGI happens."}
                            {:text "It pierces the crust of the Earth."}
                            {:text "It bounces right back into your hand."}]}}

    {:text "You find %0 here. You back away %1."
     :subs {0 {:class :item}
            1 {:class :adjective}}}

    {:text "%0 is here."
     :subs {0 {:class :actor}}}

    {:text "%0 is here %1"
     :subs {0 {:class :actor}
            1 {:class :actor-action}}}

    {:text "You find %0 here %1"
     :subs {0 {:class :actor}
            1 {:class :actor-action}}}

    {:text "%0 %1"
     :subs {0 {:class :person}
            1 {:class :dialogue}}}

    {:text "%0 is here searching for %1."
     :subs {0 {:class :actor}
            1 {:class :item}}}

    {:text "%0 is here with %1. They look %2."
     :subs {0 {:class :actor}
            1 {:class :actor}
            2 {:class :adjective}}}

    {:text "%0 follows you."
     :subs {0 {:class :actor}}}

    {:text "%0 slinks up behind you."
     :subs {0 {:class :actor}}}

    {:text "%0 wanders by,"
     :subs {0 {:class :actor}}
     :follow-ups {:optional? false
                  :options [{:text "doing algebra and shit."}
                            {:text "playing a recorder."}
                            {:text "jamming on a mouth organ."}
                            {:text "wearing a cape."}
                            {:text "casually on fire."}
                            {:text "followed by a classy balloon."}
                            {:text "whistling the theme to the Andy Griffith Show."}
                            {:text "whistling the theme to the Garry Shandling Show."}]}}

    {:text "A hollow voice intones, '%0'"
     :subs {0 {:class :intonation}}}

    {:text "Something smells %0 here."
     :subs {0 {:class :scent}}}

    {:text "It smells %0."
     :subs {0 {:class :scent}}}

    {:text "You hear %0 in the distance."
     :subs {0 {:class :noise}}}

    {:text "You hear the sound of %0 nearby."
     :subs {0 {:class :noise}}}

    {:text "The wind howls in the distance."}
    {:text "It appears abandoned."}
    {:text "Someone has been here recently."}
    {:text "There are fresh footprints here."}
    {:text "It seems that no one has been here for a long time."}
    {:text "Someone has attached marionnette wires to your hands, feet, and head."}
    {:text "Someone has left a running bulldozer here."}
    {:text "The words 'eat dulp' are spray-painted on a wall here."}
    {:text "The words 'Knifekitten lives!' are spray-painted on a wall here."}
    {:text "The words 'Hammerdog lives!' are spray-painted on a wall here."}
    {:text "Spray-painted on the wall here are the words 'Alice?! Alice?! Who the f...'. The rest is illegible."}
    {:text "An ice cream truck goes by."
     :follow-ups {:optional? false
                  :options [{:text "It's on fire."}
                            {:text "It's going by at nearly 100 mph. Somehow, a group of children are managing to keep up."}]}}
    {:text "A fire truck goes by."
     :follow-ups {:optional? false
                  :options [{:text "It's covered in ice."}
                             {:text "It's playing a funeral dirge."}]}}

    {:text "There has been significant damage from %0."
     :subs {0 {:class :disaster}}}

    {:text "You see a sign here. On it is written '%0'"
     :subs {0 {:class :sign}}}]})


(def tertiary-events
  {:tertiary-events
   [{:text "You aren't wearing any clothes."}
    {:text "Your clothes feel too small."}
    {:text "Your clothes feel too loose."}
    {:text "You're certain these aren't your clothes."}
    {:text "Your shoes are on the wrong feet."}
    {:text "Your tie feels uneven."}
    {:text "You're wearing two bowties for some reason."}
    {:text "You're not wearing any underwear."}
    {:text "You're wearing two pairs of underwear."}
    {:text "Someone is struggling with warped Tupperware nearby."}
    {:text "You do a little jig and then whistle."}
    {:text "You clap once."}
    {:text "You have socks on your hands."}
    {:text "You slowly slide your hands into your pockets. You regret this immediately."}
    {:text "You feel serene."}
    {:text "You feel nervous."}
    {:text "You feel anxious."}
    {:text "You feel cold."}
    {:text "You feel warm."}
    {:text "You aren't alone."}
    {:text "You blink really slowly."}
    {:text "You find yourself humming the theme to Too Many Cooks."}
    {:text "You hear gunfire in the distance."}
    {:text "You hear a party in the distance."}
    {:text "You hear a toilet flush nearby."}
    {:text "Someone is having fun against their will nearby."}
    {:text "You yawn."}
    {:text "You sneeze."}
    {:text "You cough."}
    {:text "You chuckle to yourself."}
    {:text "You practice frowning for awhile."}
    {:text "You begin to smile uncontrollably."}
    {:text "You wish you had your grandpappy's harmonica."}
    {:text "You are starting to feel sleepy."}
    {:text "You glance at your watch; you're running 15 minutes late."}
    {:text "You glance at your watch; somehow, you're still on time."}
    {:text "You glance at your watch; you're a little ahead of schedule."}
    {:text "You spend a few moments thinking fondly about your teeth."}
    {:text "You feel as if you're being followed."}
    {:text "A warm breeze blows by."}
    {:text "A cool breeze blows by."}
    {:text "It starts to rain."}
    {:text "It starts to snow."}
    {:text "Thunder coughs gently in the distance."}
    {:text "A basketball bounces by."}
    {:text "Something nearby is on fire."}
    {:text "You can smell something burning in the distance."}
    {:text "You look around nervously."}
    {:text "You spot a balloon stuck in a tree."}
    {:text "You spot a kitten stuck in a tree."}
    {:text "You spot an office desk in a tree."}
    {:text "You spot a bonsai tree stuck in a regular tree."}
    {:text "You see a pair of sneakers dangling on a utility line overhead."}
    {:text "Someone nearby is repeatedly zipping and unzipping a duffel bag."}

    {:text "Somehow, you've lost your %0."
     :subs {0 {:class :garment :config #{:no-article}}}}

    {:text "You hear someone nearby typing away on a manual typewriter."}
    {:text "You're catching your second wind."}
    {:text "You are starting to feel thirsty."}
    {:text "You feel better."}
    {:text "You have died."}
    {:text "You are starting to feel hungry."}]})

(def actor-actions
  {:actor-actions
   [{:text "looking %0."
     :subs {0 {:class :adjective}}}

    {:text "being chased by a swarm of balloon animals."}

    {:text "being chased by %0."
     :subs {0 {:class :person}}}

    {:text "being chased by %0 which is attached to them by a string."
     :subs {0 {:class :item}}}

    {:text "dancing furiously."}
    {:text "dancing extremely slowly."}
    {:text "shouting at an imaginary helicopter."}
    {:text "doing the Kenosha Kid."}

    {:text "thinking %0 about %1."
     :subs {0 {:class :adverb}
            1 {:class :actor}}}

    {:text "being chased around by a bee."}
    {:text "defiantly eating Scrabble tiles, one by one."}

    {:text "%0 playing the organ."
     :subs {0 {:class :adverb}}}

    {:text "organizing matches."}
    {:text "having a Guru Meditation Error."}
    {:text "juggling some balls."}
    {:text "dancing in a little circle."}
    {:text "stooping up and down like a rapper in concert."}
    {:text "drooling uncontrollably."}
    {:text "clutching a DVD of Dot & the Kangaroo."}
    {:text "clutching a DVD of BMX Bandits."}
    {:text "wearing an eyepatch."}
    {:text "wearing two eyepatches and stumbling around blindly."}
    {:text "hiding under a table."}
    {:text "hiding under a sofa."}
    {:text "hiding in the bushes."}

    {:text "munching on %0."
     :subs {0 {:class :food}}}

    {:text "pretending to be invisible."}
    {:text "having a coughing fit."}
    {:text "having a sneezing fit."}

    {:text "being menaced by %0."
     :subs {0 {:class :animal}}}

    {:text "ready to start some shit."}

    {:text "examining %0 with great confusion."
     :subs {0 {:class :item}}}]})

(def locations
  {:locations
   [{:text "dead-end"
     :type :interior
     :article "a"
     :preps ["at" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "You start to moonwalk away."}
                            {:text "Someone has painted a giant sad face here."}]}}

    {:text "ice skating rink"
     :type :exterior
     :article "an"
     :preps ["at" "in front of" "on"]
     :follow-ups {:optional? true
                  :options [{:text "It's currently on fire."}
                            {:text "Unfortunately, it's made of dry ice."}
                            {:text "A solid-gold curling stone is nearby."}
                            {:text "There are three hungry-looking zambonies here."}]}}

    {:text "movie set"
     :type :exterior
     :article "a"
     :preps ["on"]
     :follow-ups {:optional? true
                  :options [{:text "The crafts table is covered with another, smaller crafts table."}
                            {:text "A nude man strolls by."}
                            {:text "A hundred tiny dogs are here, looking menacing."}]}}

    {:text "particle board storage facility"
     :type :interior
     :article "a"
     :preps ["at" "near" "in front of" "behind" "inside"]}

    {:text "tire fire"
     :type :exterior
     :article "a"
     :preps ["at" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "It is warm and welcoming."}
                            {:text "Someone had been roasting marshmallows here."}
                            {:text "The air here is black with despair and entropy."}
                            {:text "The sky is darkened by the hellish smoke."}]}}

    {:text "dildo bonfire"
     :type :exterior
     :article "a"
     :preps ["at" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "You look closely but don't recognize any of them."}
                            {:text "The plastic hisses and creaks in the blaze."}
                            {:text "The smoke smells of vanilla."}
                            {:text "The air is dense with the echoes of unreached orgasms."}]}}

    {:text "hot tub"
     :type :interior
     :article "a"
     :preps ["in near"]
     :follow-ups {:optional? true
                  :options [{:text "The water roils and steams like water roils and steams."}
                            {:text "It's frozen solid."}
                            {:text "A basketball dances on the bubbles."}
                            {:text "You see a staircase beneath the water."}
                            {:text "Is it water or is it hydrocarbons?"}
                            {:text "It smells delicious because someone filled it with chicken soup."} ]}}

    {:text "maze of twisty little passages, all alike"
     :type :interior
     :article "a"
     :preps ["in"]}

    {:text "Burning Man"
     :type :exterior
     :preps ["at"]
     :follow-ups {:optional? true
                  :options [{:text "Oddly, no one appears to be here."}
                            {:text "A tumbleweed made out of human hair stumbles by."}
                            {:text "A dust storm is approaching."}
                            {:text "It looks like it might rain soon."}
                            {:text "Snow is gently falling."}
                            {:text "%0 is here, looking %1."
                             :subs {0 {:class :person}
                                    1 {:class :adjective}}}
                            {:text "Clearly the drugs have begun to take hold."}]}}

    {:text "Shrim Healing Center"
     :type :exterior
     :article "a"
     :preps ["in" "at" "in front of" "behind"]
     :follow-ups {:optional? true
                  :options [{:text "There are TVs in the window, all turned off."}
                            {:text "Someone has spray-painted 'I crave brown baths' here."}
                            {:text "Inside you hear the sound of repulsed joy."}
                            {:text "The door has been boarded up."}
                            {:text "%0 is patiently waiting in line by %1."
                             :subs {0 {:class :person}
                                    1 {:class :gender
                                       :case :compound}}}
                            {:text "The building looks like it has been condemned."}]}}

    {:text "quicksand"
     :type :exterior
     :article "some"
     :preps ["in" "near"]
     :follow-ups {:optional? true
                  :options [{:text "Briefly, you see a fin rise up and cruise back and forth."}
                            {:text "The surface quicksand gently sways, beckoning you..."}
                            {:text "Oddly, it smells like freshly cooked oatmeal."}]}}

    {:text "swimming pool"
     :type :exterior
     :article "a"
     :preps ["in" "at" "near"]
     :follow-ups {:optional? true
                  :options [{:text "The surface of the pool is almost entirely still. You are afraid to disturb it."}
                            {:text "The water has turned slightly murky; it does not look inviting."}
                            {:text "The surface of the pool is littered with leaves."}
                            {:text "The pool is empty."}
                            {:text "The pool is empty. A broken skateboard is nearby."}
                            {:text "A dead bird floats by."}
                            {:text "An abandoned plastic float with a dinosaur's head floats lonely nearby."}]}}

    {:text "sauna"
     :type :interior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "The wood paneling sweats sweetly in the oppressive heat."}
                            {:text "Great thunderheads of steam rise up from the rock basin, making it hard to see."}
                            {:text "The room is cold and dark. No one has been here in years."}
                            {:text "The floor is covered with cute little mushrooms."}]}}

    {:text "New York Public Library"
     :type :exterior
     :article "the"
     :preps ["at" "near" "behind" "in front of"]}

    {:text "ravine"
     :type :exterior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "It stretches out in front of you, meandering as if drunk."}
                            {:text "A giant marshmallow avalanche blocks the way ahead."}
                            {:text "A small trickle of spaghetti sauce runs down the middle."}]}}

    {:text "ditch"
     :type :exterior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "The dusty stench of aged sewage gives you a hug."}
                            {:text "It is completely blocked here by a giant boulder."}
                            {:text "A trickle of clear water runs down the middle of it."}]}}

    {:text "dump"
     :type :exterior
     :article "the"
     :preps ["at" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "In the distance, you see a hazy castle."}
                            {:text "The hill of trash shifts dangerously beneath your feet."}]}}

    {:text "dump truck"
     :type :exterior
     :article "a"
     :preps ["in" "near" "behind" "in front of" "underneath"]
     :follow-ups {:optional? true
                  :options [{:text "It's covered with a patina of black filth."}
                            {:text "Fresh off the line, it gleams bright red."}
                            {:text "The engine rumbles roughly to itself. The doors are locked."}]}}

    {:text "Starbucks"
     :type :exterior
     :article "a"
     :preps ["in" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "It is packed tightly with hipsters."}
                            {:text "There is a surprising lack of hipsters here."}
                            {:text "It reeks of slightly burnt coffee here."}]}}

    {:text "park restroom stall"
     :type :interior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "The door has been torn off its hinges."}
                            {:text "The walls are covered with violent scratches."}
                            {:text "The toilet is made of solid gold."}
                            {:text "A garden gnome greets you from the bowl."}
                            {:text "The toilet is missing."}
                            {:text "There's a basketball in the bowl."}
                            {:text "Suddenly the lights go out."}
                            {:text "The lingering scents of lemon and Lysol haunt the air."}
                            {:text "Someone has scratched your name and number on the wall."}]}}

    {:text "all-you-can-eat buffet"
     :type :interior
     :article "an"
     :preps ["at"]
     :follow-ups {:optional? true
                  :options [{:text "It looks abandoned."}
                            {:text "Bullet time means more eggrolls."}
                            {:text "Steam crowds the air."}
                            {:text "It's all gluten-free and vegan. You leave immediately."}
                            {:text "It smells of freedom and gluttony."}
                            {:text "All of the food has been replaced with wax replicas."}
                            {:text "It's in complete disarray and hasn't been tended for some time."}]}}

    {:text "grotto"
     :type :exterior
     :article "a"
     :preps ["in" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "The ceiling is sparkling with reflected light."}
                            {:text "The water is darkened with greenish-gray algae."}
                            {:text "The pool of water seems unusually deep. A lean, black fish swims in a circle."}]}}

    {:text "bedroom"
     :type :interior
     :article "your"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "It hasn't been cleaned in a long time."}
                            {:text "There's a pleasantly disgusting smell here."}
                            {:text "It's small and lightly furnished. The bed is unmade."}
                            {:text "There is nothing special about it."}
                            {:text "You notice an unusual stain in the carpet."}
                            {:text "There's an unusual stain in the carpet next to a usual stain."}
                            {:text "The ceiling fan is spinning dangerously fast."}
                            {:text "The walls are covered with %0 posters."
                             :subs {0 {:class :person}}}
                            {:text "There's a pile of clothes nearby."}]}}

    {:text "McDonald's"
     :type :exterior
     :article "a"
     :preps ["at" "in" "near" "behind" "in front of"]}

    {:text "White Castle"
     :type :exterior
     :article "a"
     :preps ["at" "in" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "It's white and vaguely castle-shaped."}
                            {:text "It smells squarely delicious."}]}}

    {:text "Taco Bell"
     :type :exterior
     :article "a"
     :preps ["at" "in" "near" "behind" "in front of"]}

    {:text "dark area"
     :type :interior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "It is pitch black here. You're likely to be eaten by %0."
                             :subs {0 {:class :actor}}}
                            {:text "It's really dark here. Like... REALLY dark."}
                            {:text "It just got slightly darker somehow."}
                            {:text "It's so dark you can taste it. Tastes like dark."}
                            {:text "It's dark here. DARK AS YOUR SOUL."}]}}

    {:text "breezy cave"
     :type :exterior
     :article "a"
     :preps ["in" "near" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "There's a constant breeze rising up from the depths."}
                            {:text "Wide and low, the cave gently slopes %0-%1 here."
                             :subs {0 {:class :direction}
                                    1 {:class :direction}}}
                            {:text "It smells of warm packing peanuts."}
                            {:text "It's really breezy. Gale-force breezy."}
                            {:text "It's seems over-oxygenated. You get light-headed."}
                            {:text "The cave seems to be breathing rapidly."}]}}

    {:text "forest"
     :type :exterior
     :article "a"
     :preps ["in" "near" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "It is a dense, dark, and tangled choke of gnarled trees, thorny underbrush, and spiky thickets."}
                            {:text "Shot through with shafts of light, the forest before you looks serene."}
                            {:text "The trees, mostly oak and spruce, sway gently in the occasional breeze."}
                            {:text "It's currently on fire."}
                            {:text "The trees are all inflated plastic."}
                            {:text "The trees ignore your incessant crying."}
                            {:text "Birds are chirping and rodents scamper through the underbrush."}]}}

    {:text "riverbed"
     :type :exterior
     :article "a"
     :preps ["in" "near"]
     :follow-ups {:optional? true
                  :options [{:text "It's dry and littered with rocks and branches."}
                            {:text "The water steadfastedly refuses to flow. It just sits there."}
                            {:text "Nearby two bears are standing on the water, defiantly."}
                            {:text "The river immediately parts and just keeps on parting."}
                            {:text "It's mostly dry, the flow of the water blocked by a beaver dams upstream."}]}}

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
     :follow-ups {:optional? true
                  :options [{:text "Oddly, all of the balls here are the same color: orange."}
                            {:text "The ballpit seems unusually deep. You can't feel the bottom."}
                            {:text "It's been filled with Rubik's Cubes."}
                            {:text "It's empty except for one orange ball at the bottom."}
                            {:text "It contains only one ball: orange and 12' across."}
                            {:text "You get the feeling someone is swimming around in there."}]}}

    {:text "airplane"
     :type :interior
     :article "an"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "There's no one else on board."}
                            {:text "You hear strange noises coming from the restroom."}
                            {:text "Somehow you have a dozen packets of pretzels."}
                            {:text "Someone drank your Fresca while you were napping."}
                            {:text "It's pitch black outside. Can grues fly?"}
                            {:text "The pilot says, 'We've reached our cruising altitude of 30 feet.'"}
                            {:text "The plane has been going straight up for hours now."}]}}

    {:text "trunk of a car"
     :type :interior
     :article "the"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "It is well upholstered."}
                            {:text "A tire iron is digging into your back a little bit."}
                            {:text "There's a half-eaten bag of Bugles here."}
                            {:text "With all the trash in here, there's barely any room for you."}
                            {:text "It's pitch black. No room for a grue, luckily."}]}}

    {:text "coffin"
     :type :interior
     :article "a"
     :preps ["in" "near" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "It is well upholstered."}
                            {:text "It smells of cotton candy in here for some reason."}
                            {:text "It smells of Aquanet in here. Makes sense."}
                            {:text "It's pitch black. It probably doesn't matter if ther's a grue here."}]}}

    {:text "hugbox"
     :type :interior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "You feel at home again."}
                            {:text "It's very warm in here. Perhaps too warm."}
                            {:text "It smells of stale sweat and lies, lies, lies..."}]}}

    {:text "haunted house"
     :type :exterior
     :article "a"
     :preps ["at" "in" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "The house shrugs under its own entropy."}
                            {:text "An orange light wanders from window to window."}
                            {:text "A sign here reads: 'As seen on TV."}
                            {:text "Endless amounts of blood pour from the windows."}
                            {:text "Looks inviting except for the corpses littering the lawn."}]}}

    {:text "graveyard"
     :type :exterior
     :article "a"
     :preps ["at" "in" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "There is a freshly laid grave nearby."}
                            {:text "There is an open grave nearby. It's empty."}
                            {:text "There is an open grave nearby. There's a phone book in it."}
                            {:text "There is an open grave nearby. It's full of %0."
                             :subs {0 {:class :drink :config #{:no-article}}}}
                            {:text "There is an open grave nearby. It's full of molten peanut butter."}
                            {:text "There are fresh footprints here."}
                            {:text "A lazy mist drifts amongst the tombstones."}
                            {:text "The tombstones have been replaced by durable plastic bricks."}
                            {:text "The Christmas lights sure make it look festive."}
                            {:text "A disco ball spins sadly from a gnarled tree."}]}}

    {:text "playground"
     :type :exterior
     :article "a"
     :preps ["in" "near" "behind" "in front of"]
     :follow-ups {:optional? true
                  :options [{:text "The equipment looks like it's never been used."}
                            {:text "Most of the equipment is missing or broken."}
                            {:text "You hear the sound of children laughing but no one else is here."}]}}

    {:text "pile of diapers"
     :type :exterior
     :article "a"
     :preps ["in" "near" "behind" "in front of" "underneath"]
     :follow-ups {:optional? true
                  :options [{:text "Some of these look awfully familiar."}]}}

    {:text "meeting"
     :type :interior
     :article "a"
     :preps ["in"]
     :follow-ups {:optional? true
                  :options [{:text "The room is crowded by tripods holding colorful charts."}
                            {:text "The projector is on, showing random photos of cats at play."}
                            {:text "The table is covered with a chalk outline."}
                            {:text "The chairs are all occupied by cobweb-encrusted skeletons."}
                            {:text "The room is almost full of balloons."}]}}

    {:text "Luby's"
     :type :exterior
     :article "a"
     :preps ["at" "in" "near" "behind" "in front of"]}

    {:text "full-contact Bible study group"
     :type :interior
     :article "a"
     :preps ["near" "behind" "in front of" "in"]
     :follow-ups {:optional? true
                  :options [{:text "No one is here."}
                            {:text "They stare at you and begin to crowl."}
                            {:text "They're all covered with cuts and bruises."}
                            {:text "They're currently scrumming over a Bible."}]}}]})

(def dialogues
  {:dialogues
   [
    {:text "asks, 'Have you ever seen an elephant throw up?'"}
    {:text "asks, 'Why am I holding this pitchfork?'"}
    {:text "asks, 'How long is a man?'"}
    {:text "asks, 'Where have you been?'"}
    {:text "asks, 'Would you like to see my collection of tiny ceiling fans?'"}
    {:text "asks, 'Which one are you?'"}
    {:text "asks, 'Can I have a hug?'"}
    {:text "asks, 'Are you following me?'"}
    {:text "asks, 'Does it smell like %0 in here to you?'"
     :subs {0 {:class :food}}}
    {:text "asks, 'Have you got a flag?'"}
    {:text "asks, 'Have you ever seen a grown man naked?'"}
    {:text "asks, 'May I use your FAX machine?'"}
    {:text "chants, 'It's time to pay the price.'"}
    {:text "mumbles, 'You can't go up against city hall.'"}
    {:text "mumbles, 'One day I'm going to burn this place to the ground.'"}
    {:text "mumbles, 'Skrillex ruined it all for everybody.'"}
    {:text "mumbles, 'I've never been to Belize.'"}
    {:text "says, 'It's true: the boot is the best McNugget shape.'"}
    {:text "says, 'Wrong answer, chief.'"}
    {:text "says, 'This is giving me the freak-out.'"}
    {:text "says, 'How unfortunate.'"}
    {:text "says, 'Well that really scrambles my eggs.'"}
    {:text "says, 'I've been waiting for you.'"}
    {:text "says, 'I can't find my heirloom clown suit.'"}

    {:text "says, 'I can't find my %0.'"
     :subs {0 {:class :garment :config #{:no-article}}}}

    {:text "says, 'No money? No hamburger!'"}
    {:text "says, 'It's like drinking a meatloaf!'"}
    {:text "says, 'Took you long enough.'"}
    {:text "says, 'I'm addicted to Kitty Lick III.'"}
    {:text "says, 'Looks like I'm not having any mayonnaise.'"}
    {:text "says, 'I'm a brown-belt in speed tai chi.'"}
    {:text "says, 'I'm stuck in a poo loop.'"}
    {:text "says, 'Well, that's a dead give away.'"}
    {:text "says, 'If you asked me to have sex with you, I wouldn't say \"no\".'"}
    {:text "says, 'I'm not an actor but I play one on television.'"}
    {:text "says, 'That dog rode on a bus by itself."}
    {:text "says, 'The time has come for me to deal with the fact that I'll never be Ed Begley, Jr.'"}
    {:text "says, 'So I guess Kevin Spacey is going to be a thing now.'"}
    {:text "says, 'I'm going to button up the top of my polo shirt to demonstrate I don't want to talk to you.'"}
    {:text "says, 'I'm in a poo trance.'"}
    {:text "says, 'I'm a source of fiber.'"}
    {:text "says, 'Surrealism is the scourage of butane.'"}
    {:text "says, 'This drink tastes like Sears!'"}
    {:text "says, 'This teenager salad tastes like a slowly closing door.'"}
    {:text "says, 'It never hurts to have a corpse open the door for you.'"}
    {:text "says, 'The Swanson TV Dinner heard 'round the world.'"}
    {:text "says, 'The membership card is edible.'"}
    {:text "says, 'That bisexual really jazzed up my latte.'"}
    {:text "says, 'Bye, Felicia."}
    {:text "shouts, 'You can't go up against city hall!'"}
    {:text "shouts, 'You can't fold a cat!'"}
    {:text "shouts, 'It keeps happening!'"}
    {:text "shouts, 'They're having a brownout in Lagos!'"}
    {:text "shouts, 'Don Quixote! Swingin' from a pipe!'"}
    {:text "shrieks, 'What's this shit I keep hearing about erections?!'"}
    {:text "shrieks, 'I'm living on the edge!'"}
    {:text "shrieks, 'Boiled soil!'"}
    {:text "shriefs, 'Baby-oil covered balloon animals for all!"}
    {:text "sighs, 'I liked you better before the hip-replacement surgery.'"}
    {:text "snarls, 'Siddown before ya fall down!'"}
    {:text "whispers, 'Why are you talking in all lowercase?'"}
    {:text "whispers, 'It puts the lotion on its skin...'"}
    {:text "whispers, 'I've always wanted to be a creepy uncle.'"}
    {:text "whispers, 'Fee was a Buddhist prodigy.'"}
    {:text "whispers, 'There squats the brown clown.'"}
    {:text "whispers, 'Sleep is unproductive and a waste of time.'"}
    {:text "whispers, 'You just lost the game.'"}
    {:text "yells, 'I warned you about stairs, bro! I told ya, dawg!'"}
    ]})

(def thoughts
  {:thoughts
   [{:text "Why don't they put mayo in the can with the tuna?"}
    {:text "%0 never has a second cup of coffee at home..."
     :subs {0 {:class :person}}}
    {:text "You can't go up against city hall."}
    {:text "I've made a huge mistake."}
    {:text "It's time to pay the price."}
    {:text "Why don't they have Double Stuf Nutter Butters?"}
    {:text "This'll all end in tears."}
    {:text "Hey! But I didn't eat the mousse!"}
    {:text "%0 still owes me a backrub."
     :subs {0 {:class :person}}}
    {:text "I wonder if I could forge us a Group 6 Access..."}]})

(def intonations
  {:intonations
   [{:text "Toast goes in the toaster."}
    {:text "These pretzels are making me thirsty."}
    {:text "For those who can make the journey, there is a place."}
    {:text "Slightly uncomfortable pleasures."}
    {:text "It is a winning cake."}
    {:text "POKE 1024,0."}
    {:text "Coitus?"}
    {:text "Pave Canada."}
    {:text "Your pilikia is all pau."}
    {:text "The owls are not what they seem."}
    {:text "Plugh."}
    {:text "Zzyzx."}
    {:text "Guch."}
    {:text "Spigot."}
    {:text "You sank my battleship."}
    {:text "Sorry, but it couldn't be helped."}
    {:text "Clean up in aisle 8A."}
    {:text "Rabbit feces."}
    {:text "Don't panic."}
    {:text "Don't panic. Oh, all right. You can panic a little bit."}
    {:text "Consider deeply the baked ham."}
    {:text "You can't go up against city hall."}]})

(def signs
  {:signs
   [{:text "Burma shave!"}
    {:text "It's time to pay the price."}
    {:text "You can't go up against city hall."}
    {:text "For those who can make the journey, there is a place."}
    {:text "Here lies Hammerdog, a dog made of hammers."}
    {:text "Here lies Knifekitten, a kitten made of knives."}
    {:text "When you're not reading this, it's written in Spanish."}
    {:text "Now you know how hard it is to say \"Irish wristwatch\"."}]})

(def books
  {:books
   [{:text "the Bible"
     :preps ["a copy of"]}

    {:text "Catcher in the Rye"
     :preps ["a copy of"]}

    {:text "Infinite Jest"
     :preps ["a copy of"]}

    {:text "Gravity's Rainbow"
     :preps ["a copy of"]}

    {:text "A Prayer for Owen Meany"
     :preps ["a copy of"]}

    {:text "The Hitchhiker's Guide to the Galaxy"
     :preps ["a copy of"]}]})

(def attacks
  {:attacks
   [{:text "%0"
     :subs {0 {:class :actor}}}
    {:text "%0"
     :subs {0 {:class :item}}}
    {:text "a judo chop"}
    {:text "a Judeo chop"}
    {:text "a filthy soap slap"}
    {:text "a rough kitty lick"}
    {:text "a high clownkick"}
    {:text "a sad testicle tug"}
    {:text "a sushi pants special"}
    {:text "a rub cut"}
    {:text "a rusty trombone"}
    {:text "an antiparticle dildo"}
    {:text "a Cleveland steamer"}
    {:text "a wet hug"}
    {:text "a prison hug"}
    {:text "a reverse hug"}
    {:text "Canadian disapproval"}
    {:text "a dangling participle"}
    {:text "a fancy uppercut"}
    {:text "a flurry of tiny kisses"}
    {:text "a forlorn sigh"}
    {:text "a balloon"}
    {:text "a spontaneous coma"}
    {:text "a smug kidney punch"}
    {:text "a bit of the ol' in-out"}
    {:text "a atomic wedge"}
    {:text "Vogon poetry"}
    {:text "a bad British accent"}
    {:text "poor grammar"}
    {:text "Conservative politics"}
    {:text "Liberal politics"}
    {:text "secret.jpg"}
    {:text "tubgirl.jpg"}
    {:text "cute kittens"}
    {:text "tumbly puppies"}
    {:text "a weaponized beard"}
    {:text "an apathetic hipster"}]})

(def directions
  {:directions
   [{:text "north"}
    {:text "northeast"}
    {:text "east"}
    {:text "southeast"}
    {:text "south"}
    {:text "southwest"}
    {:text "west"}
    {:text "northwest"}]})

(def persons
  {:persons
   [{:text "Samuel L. Jackson"
     :gender :male}

    {:text "Rob Lowe"
     :gender :male}

    {:text "Lloyd Braun"
     :gender :male}

    {:text "Bradley Whitford"
     :gender :male}

    {:text "Josh Malina"
     :gender :male}

    {:text "David Lynch"
     :gender :male}

    {:text "Allison Janney"
     :gender :female}

    {:text "Stephen Fry"
     :gender :male}

    {:text "Hugh Laurie"
     :gender :male}

    {:text "Stephen Colbert"
     :gender :male}

    {:text "Frances McDormand"
     :gender :female}

    {:text "Whoopi Goldberg"
     :gender :female}

    {:text "Katy Perry"
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

    {:text "a police officer"
     :gender :male}

    {:text "Alex Trebek"
     :gender :male}

    {:text "Craig Ferguson"
     :gender :male}

    {:text "Geoff Petersen"
     :gender :male}

    {:text "Nancy Grace"
     :gender :female}

    {:text "Lindsay Lohan"
     :gender :female}

    {:text "Ruth Buzzi"
     :gender :female}

    {:text "Jennifer Lawrence"
     :gender :female}

    {:text "Tilda Swinton"
     :gender :female}

    {:text "Peter Dinklage"
     :gender :male}

    {:text "Brad Pitt"
     :gender :male}

    {:text "Bill Maher"
     :gender :male}

    {:text "Grace Jones"
     :gender :female}

    {:text "Bill Murray"
     :gender :male}

    {:text "your mom"
     :gender :female}

    {:text "a bunch of kids"
     :gender :group}

    {:text "a crowd of Yoga enthusiasts"
     :gender :group}

    {:text "George Clooney"
     :gender :male}

    {:text "James Franco"
     :gender :male}

    {:text "Jonah Hill"
     :gender :male}

    {:text "a gas station attendant"
     :gender :male}

    {:text "Craig T. Nelson"
     :gender :male}

    {:text "Thomas Pynchon"
     :gender :male}

    {:text "Drew Olanoff"
     :gender :male}

    {:text "Louis Gray"
     :gender :male}

    {:text "@akiva"
     :gender :male}

    {:text "@jimminy"
     :gender :male}

    {:text "@veo_"
     :gender :male}

    {:text "@vmcny"
     :gender :male}

    {:text "@KamenPrime"
     :gender :male}

    {:text "@ActuallySparky"
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

    {:text "@drew"
     :gender :male}

    {:text "Bob Sacamano"
     :gender :male}

    {:text "Zombie Hunter Thompson"
     :gender :male}

    {:text "Zombie Carl Sagan"
     :gender :male}
    ]})

(def actions
  {:actions
   [{:text "attacks"}
    {:text "examines"}
    {:text "flirts with"}
    {:text "ignores"}
    {:text "imitates"}
    {:text "pets"}
    {:text "sneezes on"}
    {:text "stands uncomfortably close to"}
    {:text "tickles"}
    {:text "violently points at"}
    ]})

(def adjectives
  {:adjectives
   [{:text "afraid"}
    {:text "angry"}
    {:text "aroused"}
    {:text "bored"}
    {:text "distracted"}
    {:text "forlorn"}
    {:text "fussy"}
    {:text "happy"}
    {:text "hopeful"}
    {:text "hungry"}
    {:text "isolated"}
    {:text "nonplussed"}
    {:text "relieved"}
    {:text "sad"}
    {:text "sleepy"}
    {:text "stinky"}
    {:text "thirsty"}
    {:text "uncertain"}
    {:text "worried"}
    {:text "vexed"}
    ]})

(def adverbs
  {:adverbs
   [
    {:text "balefully"}
    {:text "bravely"}
    {:text "carefully"}
    {:text "happily"}
    {:text "lustfully"}
    {:text "mournfully"}
    {:text "proudly"}
    {:text "sadly"}
    {:text "solemnly"}
    {:text "uncertainly"}
    {:text "warily"}
    {:text "willfully"}
    {:text "wistfully"}
    ]})

(def scents
  {:scents
   [
    {:text "acrid"}
    {:text "bitter"}
    {:text "foul"}
    {:text "gross"}
    {:text "of feet"}
    {:text "of your grandfather's hair cream"}
    {:text "of warm peanut butter"}
    {:text "pleasant"}
    {:text "nice"}
    {:text "rotten"}
    {:text "sweet"}
    {:text "sour"}
    {:text "smoky"}
    ]})

(def diagnoses
  {:diagnoses
   [
    {:text "absurdly sticky"}
    {:text "drunk"}
    {:text "confused"}
    {:text "covered in bees"}
    {:text "feeling great"}
    {:text "feeling gross"}
    {:text "heavily wounded"}
    {:text "hungry"}
    {:text "lightly wounded"}
    {:text "moderately wounded"}
    {:text "near death"}
    {:text "sleepy"}
    {:text "spaghettified"}
    {:text "stoned"}
    {:text "temporarily blind"}
    {:text "temporarily deaf"}
    {:text "thirsty"}
    ]})

(def foods
  {:foods
   [
    {:text "bouquet of corndogs"
     :article "a"}

    {:text "burrito"
     :article "a"}

    {:text "corndog"
     :article "a"}

    {:text "persecution sandwich"
     :article "a"}

    {:text "tooth burger"
     :article "a"}

    {:text "weaponized cornbread"
     :article "some"}

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

    {:text "delicious churro"
     :article "a"}

    {:text "chocolate bobka"
     :article "a"}

    {:text "sweetroll"
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
     :article "an"}]})

(def drinks
  {:drinks
   [{:text "steaming gravy"
     :article "a cup of"}

    {:text "milk"
     :article "a gallon of"}

    {:text "orange juice"
     :article "a glass of"}

    {:text "tea"
     :article "some"}

    {:text "soda"
     :article "some"}

    {:text "water"
     :article "some"}

    {:text "beef broth"
     :article "some"}

    {:text "scotch"
     :article "a"}]})

(def garments
  {:garments
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

    {:text "schizophrenic mood ring"
     :article "a"}

    {:text "sweater"
     :article "a"}

    {:text "watch"
     :article "a"}]})

(def items
  {:items
   [{:text "skinny jeans"
     :article "a pair of"}

    {:text "slick pellets"
     :article "a bag of"}

    {:text "fat-soluble Jesus"
     :article "a bottle of"}

    {:text "llama treats"
     :article "a bag of"}
    {:text "parachute pants"
     :article "a pair of"}

    {:text "Breakin' 2: Electric Boogaloo"
     :article "a Laserdisc copy of"}

    {:text "magic scroll"
     :article "a"}

    {:text "no tea"}

    {:text "Snausages"
     :article "some"}

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
     :article "a"}]})

(def animals
  {:animals
   [{:text "aardvark"
     :article "an"}

    {:text "alligator"
     :article "an"}

    {:text "alpaca"
     :article "an"}

    {:text "antelope"
     :article "an"}

    {:text "ape"
     :article "an"}

    {:text "armadillo"
     :article "an"}

    {:text "baboon"
     :article "a"}

    {:text "badger"
     :article "a"}

    {:text "bat"
     :article "a"}

    {:text "bear"
     :article "a"
     :adjectives ["grizzly" "brown" "teddy"]}

    {:text "beaver"
     :article "a"}

    {:text "bison"
     :article "a"}

    {:text "boar"
     :article "a"}

    {:text "buffalo"
     :article "a"}

    {:text "bull"
     :article "a"}

    {:text "bunny"
     :article "a"}

    {:text "buzzard"
     :article "a"}

    {:text "camel"
     :article "a"}

    {:text "canary"
     :article "a"}

    {:text "capybara"
     :article "a"}

    {:text "cat"
     :article "a"
     :sounds ["purrs" "meows" "growls"]
     :adjectives ["purring" "meowing" "growling"]}

    {:text "chameleon"
     :article "a"}

    {:text "cheetah"
     :article "a"}

    {:text "chicken"
     :article "a"}

    {:text "chimpanzee"
     :article "a"}

    {:text "chinchilla"
     :article "a"}

    {:text "chipmunk"
     :article "a"}

    {:text "cougar"
     :article "a"}

    {:text "cow"
     :article "a"}

    {:text "coyote"
     :article "a"}

    {:text "crocodile"
     :article "a"}

    {:text "crow"
     :article "a"
     :sounds ["caw"]}

    {:text "dingo"
     :article "a"}

    {:text "dog"
     :article "a"
     :sounds ["pants" "barks" "growls" "whimpers"]
     :adjectives ["panting" "barking" "growling" "whimpering"]}

    {:text "dolphin"
     :article "a"}

    {:text "donkey"
     :article "a"}

    {:text "duck"
     :article "a"
     :sounds ["quacks"]
     :adjectives ["quacking"]}

    {:text "eagle"
     :article "an"}

    {:text "elephant"
     :article "an"}

    {:text "ewe"
     :article "an"}

    {:text "hawk"
     :article "a"}

    {:text "ferret"
     :article "a"}

    {:text "finch"
     :article "a"
     :sounds ["chirps"]}

    {:text "fox"
     :article "a"}

    {:text "gazelle"
     :article "a"}

    {:text "gerbil"
     :article "a"}

    {:text "gila monster"
     :article "a"}

    {:text "giraffe"
     :article "a"}

    {:text "gnu"
     :article "a"}

    {:text "goat"
     :article "a"}

    {:text "gopher"
     :article "a"}

    {:text "gorilla"
     :article "a"}

    {:text "groundhog"
     :article "a"
     :adjectives ["repeating" "familiar"]}

    {:text "guinea pig"
     :article "a"}

    {:text "hamster"
     :article "a"}

    {:text "hawk"
     :article "a"}

    {:text "hedgehog"
     :article "a"
     :adjectives ["prickly"]}

    {:text "hippopotamus"
     :article "a"}

    {:text "hog"
     :article "a"
     :sounds ["snorts" "oinks"]
     :adjuective ["snorting" "oinking"]}

    {:text "horse"
     :article "a"}

    {:text "hyena"
     :article "a"}

    {:text "ibex"
     :article "an"}

    {:text "iguana"
     :article "an"}

    {:text "impala"
     :article "an"}

    {:text "jackal"
     :article "a"}

    {:text "jaguar"
     :article "a"}

    {:text "kangaroo"
     :article "a"}

    {:text "kitten"
     :article "a"
     :sounds ["purrs" "meows" "growls"]
     :adjectives ["purring" "meowing" "growling"]}

    {:text "koala"
     :article "a"}

    {:text "lamb"
     :article "a"}

    {:text "lemur"
     :article "a"}

    {:text "leopard"
     :article "a"}

    {:text "lion"
     :article "a"}

    {:text "lizard"
     :article "a"}

    {:text "llama"
     :article "a"}

    {:text "lynx"
     :article "a"}

    {:text "magpie"
     :article "a"}

    {:text "marmot"
     :article "a"}

    {:text "mink"
     :article "a"}

    {:text "mole"
     :article "a"}

    {:text "mongoose"
     :article "a"}

    {:text "monkey"
     :article "a"}

    {:text "moose"
     :article "a"}

    {:text "mule"
     :article "a"}

    {:text "muskrat"
     :article "a"}

    {:text "moose"
     :article "a"}

    {:text "mountain goat"
     :article "a"}

    {:text "mouse"
     :article "a"}

    {:text "mule"
     :article "a"}

    {:text "muskrat"
     :article "a"}

    {:text "mynah bird"
     :article "a"}

    {:text "newt"
     :article "a"}

    {:text "ocelot"
     :article "an"}

    {:text "opossum"
     :article "an"}

    {:text "orangutan"
     :article "an"}

    {:text "otter"
     :article "an"}

    {:text "ox"
     :article "an"}

    {:text "panda"
     :article "a"}

    {:text "panther"
     :article "a"}

    {:text "parakeet"
     :article "a"}

    {:text "parrot"
     :article "a"}

    {:text "pig"
     :article "a"}

    {:text "pigeon"
     :article "a"}

    {:text "platypus"
     :article "a"}

    {:text "polar bear"
     :article "a"}

    {:text "porcupine"
     :article "a"}

    {:text "porpoise"
     :article "a"}

    {:text "prairie dog"
     :article "a"}

    {:text "puppy"
     :article "a"
     :sounds ["pants" "barks" "growls" "whimpers"]
     :adjectives ["panting" "barking" "growling" "whimpering"]}

    {:text "rabbit"
     :article "a"}

    {:text "raccoon"
     :article "a"}

    {:text "reindeer"
     :article "a"}

    {:text "rhinoceros"
     :article "a"}

    {:text "salamander"
     :article "a"}

    {:text "seal"
     :article "a"}

    {:text "serpent"
     :article "a"}

    {:text "sheep"
     :article "a"}

    {:text "shrew"
     :article "a"}

    {:text "skink"
     :article "a"}

    {:text "skunk"
     :article "a"}

    {:text "sloth"
     :article "a"}

    {:text "snake"
     :article "a"}

    {:text "squirrel"
     :article "a"}

    {:text "tiger"
     :article "a"
     :sounds ["roars"]
     :adjectives ["roaring"]}

    {:text "tortoise"
     :article "a"}

    {:text "turtle"
     :article "a"}

    {:text "walrus"
     :article "a"}

    {:text "warthog"
     :article "a"}

    {:text "weasel"
     :article "a"}

    {:text "wildcat"
     :article "a"}

    {:text "wolf"
     :article "a"}

    {:text "wombat"
     :article "a"}

    {:text "woodchuck"
     :article "a"}

    {:text "yak"
     :article "a"}

    {:text "zebra"
     :article "a"}

    ]})

(def noises
  {:noises
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
     :article "a"}]})

(def disasters
  {:disasters
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
     :article "a"}]})

(def obstacles
  {:obstacles
   [{:text "a black comb on the ground"}
    {:text "a stack of dogs going all the way to the moon"}
    {:text "Steve Buscemi's grin"}
    {:text "an obstinant balloon expert"}
    {:text "a pile of corroded Ewoks"}
    {:text "a horrible taste in your mouth"}
    {:text "a movie being shown in the wrong aspect ratio"}
    {:text "someone who has never listened to Neutral Milk Hotel"}
    {:text "the thing your aunt gave you which you don't know what it is"}
    {:text "an aquarium full of milk"}
    {:text "dubstep"}
    {:text "a 404 error"}
    {:text "Adam Sandler's career"}
    {:text "that awful man"}
    {:text "a dude who insists on putting his hand in his girlfriend's back pocket"}
    {:text "a maelstrom of hipster beards"}
    {:text "a wall of tepid soup"}
    {:text "a giant cat tongue"}
    {:text "George Lucas' neck"}
    {:text "Ruby On Rails"}
    {:text "someone who takes too many selfies"}
    {:text "a cat who has been pet against the grain"}
    {:text "substandard prequels"}
    {:text "the Scut Farkus Affair"}
    {:text "a lack of retweets"}
    {:text "a sweaty 486DX4 chip"}
    {:text "a convenient reclosing tab"}
    {:text "a lengthy German noun"}
    {:text "your own unwillingness to improve yourself"}
    {:text "%0's sheer force of will"
     :subs {0 {:class :person}}}
    {:text "%0's birthday party"
     :subs {0 {:class :person}}}
    {:text "a delicious Nutter Butter"}
    {:text "double-double Animal style"}
    {:text "someone who doesn’t know how to eat a goddamned Oreo properly"}]})

(def news
  {:news
   [{:text "Shamer Claims Bullying from Shame Shamers"}]})

(def games
  {:games
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
     :type :video}]})

(def classes
  ["events"
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
   "obstacles"
   "garments"
   "items"
   "animals"
   "noises"
   "attacks"
   "games"
   "thoughts"
   "news"
   "disasters"])
