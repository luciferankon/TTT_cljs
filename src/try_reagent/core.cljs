(ns try-reagent.core
  (:require
    [reagent.core :as r]))

;; -------------------------
;; Views

(def game-state (r/atom {}))
(def board (r/cursor game-state ["board"]))

(def board-style {
                  :display               "grid"
                  :grid-template-columns "auto auto auto"
                  :padding               "10px"
                  })

(def cell-style {
                 :height          "150px"
                 :border          "1px solid rgba(0, 0, 0, 0.8)"
                 :font-size       "60px"
                 :display         "flex"
                 :justify-content "center"
                 :align-items     "center"
                 })

(defn insert-data-in-board
  [game]
  (swap! game-state #(into % game)))

(defn fetch-game
  []
  (-> (js/fetch "http://localhost:3000")
      (.then (fn [x] (.json x)))
      (.then (fn [x] (insert-data-in-board (js->clj x))))))

(defn get-body
  [evt]
  (.stringify js/JSON (clj->js {:position (.-id (.-target evt))})))

(defn get-req-headers [evt] (clj->js {
                               :method "POST"
                               :body   (get-body evt)
                               }))

(defn update-game
  [evt]
  (-> (js/fetch "http://localhost:3000/makemove" (get-req-headers evt))
      (.then (fn [x] (.json x)))
      (.then (fn [x] (insert-data-in-board (js->clj x))))))

(defn cell [no content]
  [:div {
         :id       no
         :style    cell-style
         :on-click update-game
         } content])

(defn home-page []
  [:div {:style board-style}
   (map cell (range 1 10) @board)
   ])


;; -------------------------
;; Initialize app

(defn mount-root []
  (do
    (r/render [home-page] (.getElementById js/document "app"))
    (fetch-game)))

(defn init! []
  (mount-root))