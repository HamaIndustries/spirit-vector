package symbolics.division.spirit_vector.networking;

public class PayloadFactory {//<B extends PacketByteBuf, T> {
//    private static <P extends CustomPayload> CustomPayload.Id<P> makePayloadId(String id) {
//        return new CustomPayload.Id<>(SpiritVectorMod.id(id));
//    }
//
//    public final CustomPayload.Id<CustomizedPayload<T>> ID;
//    public final Codec<T> CODEC;
//    public final PacketCodec<PacketByteBuf, CustomizedPayload<T>> PACKET_CODEC;
//
//    private PayloadFactory (String id, Codec<T> codec, PacketCodec<B, T> packetCodec) {
//        this.ID = makePayloadId(id);
//        this.CODEC = codec;
////        this.PACKET_CODEC = packetCodec;
//    }
//
//    private final class CustomizedPayload<P> implements CustomPayload {
//        @Override
//        public Id<? extends CustomPayload> getId() {
//            return ID;
//        }
//    }
//
//    public final void registerC2S() {
//        PayloadTypeRegistry.playC2S().<CustomizedPayload<T>>register(ID, PACKET_CODEC);
//    }
}
