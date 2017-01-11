(ns nanomsg.impl
  "Implementation details of nanomsg sockets."
  (:require [nanomsg.proto :as p])
  (:import nanomsg.pubsub.PubSocket
           nanomsg.pubsub.SubSocket
           nanomsg.reqrep.ReqSocket
           nanomsg.reqrep.RepSocket
           nanomsg.pipeline.PushSocket
           nanomsg.pipeline.PullSocket
           nanomsg.pair.PairSocket
           nanomsg.bus.BusSocket
           nanomsg.Socket
           nanomsg.Nanomsg
           nanomsg.Device
           nanomsg.Poller
           nanomsg.async.AsyncSocket
           java.nio.ByteBuffer
           clojure.lang.Keyword
           clojure.lang.IFn))

(def ^:const +poll-flags-map+
  {:poll-in  Poller/POLLIN
   :poll-out Poller/POLLOUT})

(def ^:const +socket-types-map+
  {:pub #(PubSocket.)
   :sub #(SubSocket.)
   :req #(ReqSocket.)
   :rep #(RepSocket.)
   :bus #(BusSocket.)
   :pair #(PairSocket.)
   :push #(PushSocket.)
   :pull #(PullSocket.)})

(extend-type Socket
  p/ISocket
  (-bind [socket endpoint]
    (.bind socket endpoint))
  (-connect [socket endpoint]
    (.connect socket endpoint))
  (-subscribe [socket ^String topic]
    (.subscribe socket topic))
  (-unsubscribe [socket ^String topic]
    (.unsubscribe socket topic))
  (-recv [socket blocking]
    (.recv socket blocking))
  (-send [socket data ^java.util.EnumSet blocking]
    (let [^ByteBuffer data (p/-byte-buffer data)]
      (.send socket data blocking)))
  (-send-timeout [socket]
    (.getSocketOpt socket nanomsg.Nanomsg$SocketOption/NN_SNDTIMEO))
  (-send-timeout! [socket ^long timeout]
    (.setSocketOpt socket nanomsg.Nanomsg$SocketOption/NN_SNDTIMEO (int timeout)))
  (-recv-timeout [socket]
    (.getSocketOpt socket nanomsg.Nanomsg$SocketOption/NN_RCVTIMEO))
  (-recv-timeout! [socket ^long timeout]
    (.setSocketOpt socket nanomsg.Nanomsg$SocketOption/NN_RCVTIMEO (int timeout))))

(extend-type Poller
  p/IPoller
  (-register [p socket flags]
    (let [flags (apply bit-or 0 0 (keep +poll-flags-map+ flags))]
      (.register p socket flags)))
  (-unregister [p socket]
    (.unregister p socket))
  (-poll [p ms]
    (.poll p ms))
  (-readable? [p socket]
    (.isReadable p socket))
  (-writable? [p socket]
    (.isWritable p socket)))

(extend-protocol p/ISocketData
  (Class/forName "[B")
  (-byte-buffer [b]
    (ByteBuffer/wrap b))

  java.nio.ByteBuffer
  (-byte-buffer [b] b)

  java.lang.String
  (-byte-buffer [s]
    (p/-byte-buffer (.getBytes s "UTF-8"))))
