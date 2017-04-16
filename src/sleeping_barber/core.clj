(ns sleeping-barber.core)

(def barber-shop (atom {:barber-chair {}
                        :waiting-room nil
                        :total-served 0}))

(defn- update-waiting-room
  [new-customer waiting-room]
  (if (< (count waiting-room) 3)
    (conj waiting-room new-customer)
    (do
      ;;(println "customer id" (new-customer :id) "left: the barber is too busy.")
      waiting-room)))

(defn free-seat-into-waiting-room
  [old-customer-id waiting-room]
  (filter (fn [x] (not (= old-customer-id (x :id)))) waiting-room))

(defn sit-in-barber-chair
  [customer]
  (swap! barber-shop assoc :barber-chair customer))

(defn is-barber-busy?
  []
  (empty? (@barber-shop :barber-chair)))


(defn new-customer-arrives! [new-customer]
  (let [{:keys [barber-chair waiting-room] :as all} @barber-shop
        new-waiting-room (update-waiting-room new-customer waiting-room)]
    (if (and is-barber-busy?)
      (swap! barber-shop assoc :waiting-room new-waiting-room)
      (do
        (sit-in-barber-chair new-customer)
        do-haircut))))

(defn create-customer
  "create a new customer with a random delay between 10 and 30 milliseconds"
  []
  (let [customer-delay (+ 10 (rand-int 20))
        id (rand-int 1000)]
    (future
      (Thread/sleep customer-delay)
      (new-customer-arrives! {:id id})
     ;; (println "new customer" id "arrived!")
      )))


(defn- empty-barber-chair!
  []
  (swap! barber-shop dissoc :barber-chair))

(defn- inc-served!
  []
  (let [new-value (inc (@barber-shop :total-served))]
    (swap! barber-shop assoc :total-served new-value)))

(defn do-haircut
  "do the haircut: wait 20 milliseconds and empty the barber-chair"
  [customer-id]
  (future
    (Thread/sleep 20)
    (empty-barber-chair!)
    (inc-served!)
    (println "")))

(defn serve-a-customer!
  "move a customer from the waiting-room to the barber-chair"
  [customer-to-serve]
  (let [customer-id (customer-to-serve :id)]
    (if (and (empty? (@barber-shop :barber-chair))
             (not (empty? (@barber-shop :waiting-room))))
      (do
        (swap! barber-shop assoc :waiting-room (free-seat-into-waiting-room customer-id (@barber-shop :waiting-room)))
        (sit-in-barber-chair customer-to-serve)
        (do-haircut customer-id)))))

;;(@barber-shop :waiting-room)
;;(@barber-shop :barber-chair)
;;(@barber-shop :total-served)

(def timer (atom true))
;;(swap! timer :assoc false)
;;(swap! timer :assoc true)

(defn main [time]
  (do (println "---- BARBER OPEN! ----")
      (future
        (Thread/sleep time)
        (swap! timer :assoc false))
      (while (boolean @timer)
        (do
          (create-customer)
          (if (not (empty? (@barber-shop :waiting-room)))
            (serve-a-customer! (first (@barber-shop :waiting-room))))))
      (println "total customer served:" (@barber-shop :total-served))
      (println "--------------------------")))

(main 60000)
