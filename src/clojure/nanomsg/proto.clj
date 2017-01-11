(ns nanomsg.proto
  "Protocol definitions for nanomsg sockets.")

(defprotocol ISocket
  "Common socket protocol."
  (-bind [_ dir])
  (-connect [_ dir])
  (-subscribe [_ pattern])
  (-unsubscribe [_ pattern])
  (-send [_ data opt])
  (-recv [_ opt])
  (-send-timeout [_])
  (-send-timeout! [_ timeout])
  (-recv-timeout [_])
  (-recv-timeout! [_ timeout])
  )

(defprotocol IPoller
  (-register [_ socket flags])
  (-unregister [_ socket])
  (-readable? [_ socket])
  (-writable? [_ socket])
  (-poll [_ ms]))

(defprotocol ISocketData
  "Common interface for data that can be
  sended through nanomsg socket."
  (-byte-buffer [_] "Get a byte array representation."))
