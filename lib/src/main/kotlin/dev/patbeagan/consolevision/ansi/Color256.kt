package dev.patbeagan.consolevision.ansi

import dev.patbeagan.consolevision.util.distance
import kotlin.math.abs

/**
 * A resource that stands for a color
 * This enum contains the 256 colors that are defined by ANSI,
 * along with their hexcode values.
 */
enum class Color256(
    /**
     * The hexcode for the color that this [Color256] represents
     */
    val color: Int,
    /**
     * The id of this color within the ANSI 256 color set
     */
    val number: Int
) {
    Color0(0x000000C, 0),
    Color1(0x800000C, 1),
    Color2(0x008000C, 2),
    Color3(0x808000C, 3),
    Color4(0x000080C, 4),
    Color5(0x800080C, 5),
    Color6(0x008080C, 6),
    Color7(0xc0c0c0, 7),

    Color8(0x808080C, 8),
    Color9(0xff0000C, 9),
    Color10(0x00ff00C, 10),
    Color11(0xffff00C, 11),
    Color12(0x0000ffC, 12),
    Color13(0xff00ffC, 13),
    Color14(0x00ffffC, 14),
    Color15(0xffffff, 15),

    Color16(0x000000, 16),
    Color17(0x00005f, 17),
    Color18(0x000087, 18),
    Color19(0x0000af, 19),
    Color20(0x0000d7, 20),
    Color21(0x0000ff, 21),

    Color22(0x005f00, 22),
    Color23(0x005f5f, 23),
    Color24(0x005f87, 24),
    Color25(0x005faf, 25),
    Color26(0x005fd7, 26),
    Color27(0x005fff, 27),

    Color28(0x008700, 28),
    Color29(0x00875f, 29),
    Color30(0x008787, 30),
    Color31(0x0087af, 31),
    Color32(0x0087d7, 32),
    Color33(0x0087ff, 33),

    Color34(0x00af00, 34),
    Color35(0x00af5f, 35),
    Color36(0x00af87, 36),
    Color37(0x00afaf, 37),
    Color38(0x00afd7, 38),
    Color39(0x00afff, 39),

    Color40(0x00d700, 40),
    Color41(0x00d75f, 41),
    Color42(0x00d787, 42),
    Color43(0x00d7af, 43),
    Color44(0x00d7d7, 44),
    Color45(0x00d7ff, 45),

    Color46(0x00ff00, 46),
    Color47(0x00ff5f, 47),
    Color48(0x00ff87, 48),
    Color49(0x00ffaf, 49),
    Color50(0x00ffd7, 50),
    Color51(0x00ffff, 51),

    Color52(0x5f0000, 52),
    Color53(0x5f005f, 53),
    Color54(0x5f0087, 54),
    Color55(0x5f00af, 55),
    Color56(0x5f00d7, 56),
    Color57(0x5f00ff, 57),

    Color58(0x5f5f00, 58),
    Color59(0x5f5f5f, 59),
    Color60(0x5f5f87, 60),
    Color61(0x5f5faf, 61),
    Color62(0x5f5fd7, 62),
    Color63(0x5f5fff, 63),

    Color64(0x5f8700, 64),
    Color65(0x5f875f, 65),
    Color66(0x5f8787, 66),
    Color67(0x5f87af, 67),
    Color68(0x5f87d7, 68),
    Color69(0x5f87ff, 69),

    Color70(0x5faf00, 70),
    Color71(0x5faf5f, 71),
    Color72(0x5faf87, 72),
    Color73(0x5fafaf, 73),
    Color74(0x5fafd7, 74),
    Color75(0x5fafff, 75),

    Color76(0x5fd700, 76),
    Color77(0x5fd75f, 77),
    Color78(0x5fd787, 78),
    Color79(0x5fd7af, 79),
    Color80(0x5fd7d7, 80),
    Color81(0x5fd7ff, 81),

    Color82(0x5fff00, 82),
    Color83(0x5fff5f, 83),
    Color84(0x5fff87, 84),
    Color85(0x5fffaf, 85),
    Color86(0x5fffd7, 86),
    Color87(0x5fffff, 87),

    Color88(0x870000, 88),
    Color89(0x87005f, 89),
    Color90(0x870087, 90),
    Color91(0x8700af, 91),
    Color92(0x8700d7, 92),
    Color93(0x8700ff, 93),

    Color94(0x875f00, 94),
    Color95(0x875f5f, 95),
    Color96(0x875f87, 96),
    Color97(0x875faf, 97),
    Color98(0x875fd7, 98),
    Color99(0x875fff, 99),

    Color100(0x878700, 100),
    Color101(0x87875f, 101),
    Color102(0x878787, 102),
    Color103(0x8787af, 103),
    Color104(0x8787d7, 104),
    Color105(0x8787ff, 105),

    Color106(0x87af00, 106),
    Color107(0x87af5f, 107),
    Color108(0x87af87, 108),
    Color109(0x87afaf, 109),
    Color110(0x87afd7, 110),
    Color111(0x87afff, 111),

    Color112(0x87d700, 112),
    Color113(0x87d75f, 113),
    Color114(0x87d787, 114),
    Color115(0x87d7af, 115),
    Color116(0x87d7d7, 116),
    Color117(0x87d7ff, 117),

    Color118(0x87ff00, 118),
    Color119(0x87ff5f, 119),
    Color120(0x87ff87, 120),
    Color121(0x87ffaf, 121),
    Color122(0x87ffd7, 122),
    Color123(0x87ffff, 123),

    Color124(0xaf0000, 124),
    Color125(0xaf005f, 125),
    Color126(0xaf0087, 126),
    Color127(0xaf00af, 127),
    Color128(0xaf00d7, 128),
    Color129(0xaf00ff, 129),

    Color130(0xaf5f00, 130),
    Color131(0xaf5f5f, 131),
    Color132(0xaf5f87, 132),
    Color133(0xaf5faf, 133),
    Color134(0xaf5fd7, 134),
    Color135(0xaf5fff, 135),

    Color136(0xaf8700, 136),
    Color137(0xaf875f, 137),
    Color138(0xaf8787, 138),
    Color139(0xaf87af, 139),
    Color140(0xaf87d7, 140),
    Color141(0xaf87ff, 141),

    Color142(0xafaf00, 142),
    Color143(0xafaf5f, 143),
    Color144(0xafaf87, 144),
    Color145(0xafafaf, 145),
    Color146(0xafafd7, 146),
    Color147(0xafafff, 147),

    Color148(0xafd700, 148),
    Color149(0xafd75f, 149),
    Color150(0xafd787, 150),
    Color151(0xafd7af, 151),
    Color152(0xafd7d7, 152),
    Color153(0xafd7ff, 153),

    Color154(0xafff00, 154),
    Color155(0xafff5f, 155),
    Color156(0xafff87, 156),
    Color157(0xafffaf, 157),
    Color158(0xafffd7, 158),
    Color159(0xafffff, 159),

    Color160(0xd70000, 160),
    Color161(0xd7005f, 161),
    Color162(0xd70087, 162),
    Color163(0xd700af, 163),
    Color164(0xd700d7, 164),
    Color165(0xd700ff, 165),

    Color166(0xd75f00, 166),
    Color167(0xd75f5f, 167),
    Color168(0xd75f87, 168),
    Color169(0xd75faf, 169),
    Color170(0xd75fd7, 170),
    Color171(0xd75fff, 171),

    Color172(0xd78700, 172),
    Color173(0xd7875f, 173),
    Color174(0xd78787, 174),
    Color175(0xd787af, 175),
    Color176(0xd787d7, 176),
    Color177(0xd787ff, 177),

    Color178(0xd7af00, 178),
    Color179(0xd7af5f, 179),
    Color180(0xd7af87, 180),
    Color181(0xd7afaf, 181),
    Color182(0xd7afd7, 182),
    Color183(0xd7afff, 183),

    Color184(0xd7d700, 184),
    Color185(0xd7d75f, 185),
    Color186(0xd7d787, 186),
    Color187(0xd7d7af, 187),
    Color188(0xd7d7d7, 188),
    Color189(0xd7d7ff, 189),

    Color190(0xd7ff00, 190),
    Color191(0xd7ff5f, 191),
    Color192(0xd7ff87, 192),
    Color193(0xd7ffaf, 193),
    Color194(0xd7ffd7, 194),
    Color195(0xd7ffff, 195),

    Color196(0xff0000, 196),
    Color197(0xff005f, 197),
    Color198(0xff0087, 198),
    Color199(0xff00af, 199),
    Color200(0xff00d7, 200),
    Color201(0xff00ff, 201),

    Color202(0xff5f00, 202),
    Color203(0xff5f5f, 203),
    Color204(0xff5f87, 204),
    Color205(0xff5faf, 205),
    Color206(0xff5fd7, 206),
    Color207(0xff5fff, 207),

    Color208(0xff8700, 208),
    Color209(0xff875f, 209),
    Color210(0xff8787, 210),
    Color211(0xff87af, 211),
    Color212(0xff87d7, 212),
    Color213(0xff87ff, 213),

    Color214(0xffaf00, 214),
    Color215(0xffaf5f, 215),
    Color216(0xffaf87, 216),
    Color217(0xffafaf, 217),
    Color218(0xffafd7, 218),
    Color219(0xffafff, 219),

    Color220(0xffd700, 220),
    Color221(0xffd75f, 221),
    Color222(0xffd787, 222),
    Color223(0xffd7af, 223),
    Color224(0xffd7d7, 224),
    Color225(0xffd7ff, 225),

    Color226(0xffff00, 226),
    Color227(0xffff5f, 227),
    Color228(0xffff87, 228),
    Color229(0xffffaf, 229),
    Color230(0xffffd7, 230),
    Color231(0xffffff, 231),

    Color232(0x080808, 232),
    Color233(0x121212, 233),
    Color234(0x1c1c1c, 234),
    Color235(0x262626, 235),
    Color236(0x303030, 236),
    Color237(0x3a3a3a, 237),

    Color238(0x444444, 238),
    Color239(0x4e4e4e, 239),
    Color240(0x585858, 240),
    Color241(0x606060, 241),
    Color242(0x666666, 242),
    Color243(0x767676, 243),

    Color244(0x808080, 244),
    Color245(0x8a8a8a, 245),
    Color246(0x949494, 246),
    Color247(0x9e9e9e, 247),
    Color248(0xa8a8a8, 248),
    Color249(0xb2b2b2, 249),

    Color250(0xbcbcbc, 250),
    Color251(0xc6c6c6, 251),
    Color252(0xd0d0d0, 252),
    Color253(0xdadada, 253),
    Color254(0xe4e4e4, 254),
    Color255(0xeeeeee, 255);

    companion object {
        /**
         * A list of all the greyscale colors in the 256 color set.
         */
        val greyscale: List<Color256> = listOf(
            Color232,
            Color233,
            Color234,
            Color235,
            Color236,
            Color237,
            Color238,
            Color239,
            Color240,
            Color241,
            Color242,
            Color243,
            Color244,
            Color245,
            Color246,
            Color247,
            Color248,
            Color249,
            Color250,
            Color251,
            Color252,
            Color253,
            Color254,
            Color255
        )
    }
}
