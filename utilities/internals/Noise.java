// Noise.java
//
// MIT License
//
// Copyright(c) 2017 Jordan Peck
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package me.wonk2.utilities.internals;

import me.wonk2.utilities.DFUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Noise {
	
	public enum FractalType {
		BROWNIAN,
		BILLOW,
		RIGID
	}
	
	public enum CellularReturnType {
		VORONOI,
		PRIMARY_DISTANCE,
		SECONDARY_DISTANCE,
		ADDITIVE_DISTANCES,
		SUBTRACTIVE_DISTANCES,
		MULTIPLICATIVE_DISTANCES,
		DIVISIVE_DISTANCES
	}
	
	public enum CellEdgeType {
		EUCLIDEAN,
		MANHATTAN,
		NATURAL
	}
	
	private static class Float3 {
		private final float x;
		private final float y;
		private final float z;
		
		private Float3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	private static final Float3[] GRAD_3D = {
		new Float3(1, 1, 0), new Float3(-1, 1, 0), new Float3(1, -1, 0), new Float3(-1, -1, 0),
		new Float3(1, 0, 1), new Float3(-1, 0, 1), new Float3(1, 0, -1), new Float3(-1, 0, -1),
		new Float3(0, 1, 1), new Float3(0, -1, 1), new Float3(0, 1, -1), new Float3(0, -1, -1),
		new Float3(1, 1, 0), new Float3(0, -1, 1), new Float3(-1, 1, 0), new Float3(0, -1, -1),
	};
	
	private static final Float3[] CELL_3D = {
		new Float3(0.1453787434f, -0.4149781685f, -0.0956981749f), new Float3(-0.01242829687f, -0.1457918398f, -0.4255470325f), new Float3(0.2877979582f, -0.02606483451f, -0.3449535616f), new Float3(-0.07732986802f, 0.2377094325f, 0.3741848704f), new Float3(0.1107205875f, -0.3552302079f, -0.2530858567f), new Float3(0.2755209141f, 0.2640521179f, -0.238463215f), new Float3(0.294168941f, 0.1526064594f, 0.3044271714f), new Float3(0.4000921098f, -0.2034056362f, 0.03244149937f),
		new Float3(-0.1697304074f, 0.3970864695f, -0.1265461359f), new Float3(-0.1483224484f, -0.3859694688f, 0.1775613147f), new Float3(0.2623596946f, -0.2354852944f, 0.2796677792f), new Float3(-0.2709003183f, 0.3505271138f, -0.07901746678f), new Float3(-0.03516550699f, 0.3885234328f, 0.2243054374f), new Float3(-0.1267712655f, 0.1920044036f, 0.3867342179f), new Float3(0.02952021915f, 0.4409685861f, 0.08470692262f), new Float3(-0.2806854217f, -0.266996757f, 0.2289725438f),
		new Float3(-0.171159547f, 0.2141185563f, 0.3568720405f), new Float3(0.2113227183f, 0.3902405947f, -0.07453178509f), new Float3(-0.1024352839f, 0.2128044156f, -0.3830421561f), new Float3(-0.3304249877f, -0.1566986703f, 0.2622305365f), new Float3(0.2091111325f, 0.3133278055f, -0.2461670583f), new Float3(0.344678154f, -0.1944240454f, -0.2142341261f), new Float3(0.1984478035f, -0.3214342325f, -0.2445373252f), new Float3(-0.2929008603f, 0.2262915116f, 0.2559320961f),
		new Float3(-0.1617332831f, 0.006314769776f, -0.4198838754f), new Float3(-0.3582060271f, -0.148303178f, -0.2284613961f), new Float3(-0.1852067326f, -0.3454119342f, -0.2211087107f), new Float3(0.3046301062f, 0.1026310383f, 0.314908508f), new Float3(-0.03816768434f, -0.2551766358f, -0.3686842991f), new Float3(-0.4084952196f, 0.1805950793f, 0.05492788837f), new Float3(-0.02687443361f, -0.2749741471f, 0.3551999201f), new Float3(-0.03801098351f, 0.3277859044f, 0.3059600725f),
		new Float3(0.2371120802f, 0.2900386767f, -0.2493099024f), new Float3(0.4447660503f, 0.03946930643f, 0.05590469027f), new Float3(0.01985147278f, -0.01503183293f, -0.4493105419f), new Float3(0.4274339143f, 0.03345994256f, -0.1366772882f), new Float3(-0.2072988631f, 0.2871414597f, -0.2776273824f), new Float3(-0.3791240978f, 0.1281177671f, 0.2057929936f), new Float3(-0.2098721267f, -0.1007087278f, -0.3851122467f), new Float3(0.01582798878f, 0.4263894424f, 0.1429738373f),
		new Float3(-0.1888129464f, -0.3160996813f, -0.2587096108f), new Float3(0.1612988974f, -0.1974805082f, -0.3707885038f), new Float3(-0.08974491322f, 0.229148752f, -0.3767448739f), new Float3(0.07041229526f, 0.4150230285f, -0.1590534329f), new Float3(-0.1082925611f, -0.1586061639f, 0.4069604477f), new Float3(0.2474100658f, -0.3309414609f, 0.1782302128f), new Float3(-0.1068836661f, -0.2701644537f, -0.3436379634f), new Float3(0.2396452163f, 0.06803600538f, -0.3747549496f),
		new Float3(-0.3063886072f, 0.2597428179f, 0.2028785103f), new Float3(0.1593342891f, -0.3114350249f, -0.2830561951f), new Float3(0.2709690528f, 0.1412648683f, -0.3303331794f), new Float3(-0.1519780427f, 0.3623355133f, 0.2193527988f), new Float3(0.1699773681f, 0.3456012883f, 0.2327390037f), new Float3(-0.1986155616f, 0.3836276443f, -0.1260225743f), new Float3(-0.1887482106f, -0.2050154888f, -0.353330953f), new Float3(0.2659103394f, 0.3015631259f, -0.2021172246f),
		new Float3(-0.08838976154f, -0.4288819642f, -0.1036702021f), new Float3(-0.04201869311f, 0.3099592485f, 0.3235115047f), new Float3(-0.3230334656f, 0.201549922f, -0.2398478873f), new Float3(0.2612720941f, 0.2759854499f, -0.2409749453f), new Float3(0.385713046f, 0.2193460345f, 0.07491837764f), new Float3(0.07654967953f, 0.3721732183f, 0.241095919f), new Float3(0.4317038818f, -0.02577753072f, 0.1243675091f), new Float3(-0.2890436293f, -0.3418179959f, -0.04598084447f),
		new Float3(-0.2201947582f, 0.383023377f, -0.08548310451f), new Float3(0.4161322773f, -0.1669634289f, -0.03817251927f), new Float3(0.2204718095f, 0.02654238946f, -0.391391981f), new Float3(-0.1040307469f, 0.3890079625f, -0.2008741118f), new Float3(-0.1432122615f, 0.371614387f, -0.2095065525f), new Float3(0.3978380468f, -0.06206669342f, 0.2009293758f), new Float3(-0.2599274663f, 0.2616724959f, -0.2578084893f), new Float3(0.4032618332f, -0.1124593585f, 0.1650235939f),
		new Float3(-0.08953470255f, -0.3048244735f, 0.3186935478f), new Float3(0.118937202f, -0.2875221847f, 0.325092195f), new Float3(0.02167047076f, -0.03284630549f, -0.4482761547f), new Float3(-0.3411343612f, 0.2500031105f, 0.1537068389f), new Float3(0.3162964612f, 0.3082064153f, -0.08640228117f), new Float3(0.2355138889f, -0.3439334267f, -0.1695376245f), new Float3(-0.02874541518f, -0.3955933019f, 0.2125550295f), new Float3(-0.2461455173f, 0.02020282325f, -0.3761704803f),
		new Float3(0.04208029445f, -0.4470439576f, 0.02968078139f), new Float3(0.2727458746f, 0.2288471896f, -0.2752065618f), new Float3(-0.1347522818f, -0.02720848277f, -0.4284874806f), new Float3(0.3829624424f, 0.1231931484f, -0.2016512234f), new Float3(-0.3547613644f, 0.1271702173f, 0.2459107769f), new Float3(0.2305790207f, 0.3063895591f, 0.2354968222f), new Float3(-0.08323845599f, -0.1922245118f, 0.3982726409f), new Float3(0.2993663085f, -0.2619918095f, -0.2103333191f),
		new Float3(-0.2154865723f, 0.2706747713f, 0.287751117f), new Float3(0.01683355354f, -0.2680655787f, -0.3610505186f), new Float3(0.05240429123f, 0.4335128183f, -0.1087217856f), new Float3(0.00940104872f, -0.4472890582f, 0.04841609928f), new Float3(0.3465688735f, 0.01141914583f, -0.2868093776f), new Float3(-0.3706867948f, -0.2551104378f, 0.003156692623f), new Float3(0.2741169781f, 0.2139972417f, -0.2855959784f), new Float3(0.06413433865f, 0.1708718512f, 0.4113266307f),
		new Float3(-0.388187972f, -0.03973280434f, -0.2241236325f), new Float3(0.06419469312f, -0.2803682491f, 0.3460819069f), new Float3(-0.1986120739f, -0.3391173584f, 0.2192091725f), new Float3(-0.203203009f, -0.3871641506f, 0.1063600375f), new Float3(-0.1389736354f, -0.2775901578f, -0.3257760473f), new Float3(-0.06555641638f, 0.342253257f, -0.2847192729f), new Float3(-0.2529246486f, -0.2904227915f, 0.2327739768f), new Float3(0.1444476522f, 0.1069184044f, 0.4125570634f),
		new Float3(-0.3643780054f, -0.2447099973f, -0.09922543227f), new Float3(0.4286142488f, -0.1358496089f, -0.01829506817f), new Float3(0.165872923f, -0.3136808464f, -0.2767498872f), new Float3(0.2219610524f, -0.3658139958f, 0.1393320198f), new Float3(0.04322940318f, -0.3832730794f, 0.2318037215f), new Float3(-0.08481269795f, -0.4404869674f, -0.03574965489f), new Float3(0.1822082075f, -0.3953259299f, 0.1140946023f), new Float3(-0.3269323334f, 0.3036542563f, 0.05838957105f),
		new Float3(-0.4080485344f, 0.04227858267f, -0.184956522f), new Float3(0.2676025294f, -0.01299671652f, 0.36155217f), new Float3(0.3024892441f, -0.1009990293f, -0.3174892964f), new Float3(0.1448494052f, 0.425921681f, -0.0104580805f), new Float3(0.4198402157f, 0.08062320474f, 0.1404780841f), new Float3(-0.3008872161f, -0.333040905f, -0.03241355801f), new Float3(0.3639310428f, -0.1291284382f, -0.2310412139f), new Float3(0.3295806598f, 0.0184175994f, -0.3058388149f),
		new Float3(0.2776259487f, -0.2974929052f, -0.1921504723f), new Float3(0.4149000507f, -0.144793182f, -0.09691688386f), new Float3(0.145016715f, -0.0398992945f, 0.4241205002f), new Float3(0.09299023471f, -0.299732164f, -0.3225111565f), new Float3(0.1028907093f, -0.361266869f, 0.247789732f), new Float3(0.2683057049f, -0.07076041213f, -0.3542668666f), new Float3(-0.4227307273f, -0.07933161816f, -0.1323073187f), new Float3(-0.1781224702f, 0.1806857196f, -0.3716517945f),
		new Float3(0.4390788626f, -0.02841848598f, -0.09435116353f), new Float3(0.2972583585f, 0.2382799621f, -0.2394997452f), new Float3(-0.1707002821f, 0.2215845691f, 0.3525077196f), new Float3(0.3806686614f, 0.1471852559f, -0.1895464869f), new Float3(-0.1751445661f, -0.274887877f, 0.3102596268f), new Float3(-0.2227237566f, -0.2316778837f, 0.3149912482f), new Float3(0.1369633021f, 0.1341343041f, -0.4071228836f), new Float3(-0.3529503428f, -0.2472893463f, -0.129514612f),
		new Float3(-0.2590744185f, -0.2985577559f, -0.2150435121f), new Float3(-0.3784019401f, 0.2199816631f, -0.1044989934f), new Float3(-0.05635805671f, 0.1485737441f, 0.4210102279f), new Float3(0.3251428613f, 0.09666046873f, -0.2957006485f), new Float3(-0.4190995804f, 0.1406751354f, -0.08405978803f), new Float3(-0.3253150961f, -0.3080335042f, -0.04225456877f), new Float3(0.2857945863f, -0.05796152095f, 0.3427271751f), new Float3(-0.2733604046f, 0.1973770973f, -0.2980207554f),
		new Float3(0.219003657f, 0.2410037886f, -0.3105713639f), new Float3(0.3182767252f, -0.271342949f, 0.1660509868f), new Float3(-0.03222023115f, -0.3331161506f, -0.300824678f), new Float3(-0.3087780231f, 0.1992794134f, -0.2596995338f), new Float3(-0.06487611647f, -0.4311322747f, 0.1114273361f), new Float3(0.3921171432f, -0.06294284106f, -0.2116183942f), new Float3(-0.1606404506f, -0.358928121f, -0.2187812825f), new Float3(-0.03767771199f, -0.2290351443f, 0.3855169162f),
		new Float3(0.1394866832f, -0.3602213994f, 0.2308332918f), new Float3(-0.4345093872f, 0.005751117145f, 0.1169124335f), new Float3(-0.1044637494f, 0.4168128432f, -0.1336202785f), new Float3(0.2658727501f, 0.2551943237f, 0.2582393035f), new Float3(0.2051461999f, 0.1975390727f, 0.3484154868f), new Float3(-0.266085566f, 0.23483312f, 0.2766800993f), new Float3(0.07849405464f, -0.3300346342f, -0.2956616708f), new Float3(-0.2160686338f, 0.05376451292f, -0.3910546287f),
		new Float3(-0.185779186f, 0.2148499206f, 0.3490352499f), new Float3(0.02492421743f, -0.3229954284f, -0.3123343347f), new Float3(-0.120167831f, 0.4017266681f, 0.1633259825f), new Float3(-0.02160084693f, -0.06885389554f, 0.4441762538f), new Float3(0.2597670064f, 0.3096300784f, 0.1978643903f), new Float3(-0.1611553854f, -0.09823036005f, 0.4085091653f), new Float3(-0.3278896792f, 0.1461670309f, 0.2713366126f), new Float3(0.2822734956f, 0.03754421121f, -0.3484423997f),
		new Float3(0.03169341113f, 0.347405252f, -0.2842624114f), new Float3(0.2202613604f, -0.3460788041f, -0.1849713341f), new Float3(0.2933396046f, 0.3031973659f, 0.1565989581f), new Float3(-0.3194922995f, 0.2453752201f, -0.200538455f), new Float3(-0.3441586045f, -0.1698856132f, -0.2349334659f), new Float3(0.2703645948f, -0.3574277231f, 0.04060059933f), new Float3(0.2298568861f, 0.3744156221f, 0.0973588921f), new Float3(0.09326603877f, -0.3170108894f, 0.3054595587f),
		new Float3(-0.1116165319f, -0.2985018719f, 0.3177080142f), new Float3(0.2172907365f, -0.3460005203f, -0.1885958001f), new Float3(0.1991339479f, 0.3820341668f, -0.1299829458f), new Float3(-0.0541918155f, -0.2103145071f, 0.39412061f), new Float3(0.08871336998f, 0.2012117383f, 0.3926114802f), new Float3(0.2787673278f, 0.3505404674f, 0.04370535101f), new Float3(-0.322166438f, 0.3067213525f, 0.06804996813f), new Float3(-0.4277366384f, 0.132066775f, 0.04582286686f),
		new Float3(0.240131882f, -0.1612516055f, 0.344723946f), new Float3(0.1448607981f, -0.2387819045f, 0.3528435224f), new Float3(-0.3837065682f, -0.2206398454f, 0.08116235683f), new Float3(-0.4382627882f, -0.09082753406f, -0.04664855374f), new Float3(-0.37728353f, 0.05445141085f, 0.2391488697f), new Float3(0.1259579313f, 0.348394558f, 0.2554522098f), new Float3(-0.1406285511f, -0.270877371f, -0.3306796947f), new Float3(-0.1580694418f, 0.4162931958f, -0.06491553533f),
		new Float3(0.2477612106f, -0.2927867412f, -0.2353514536f), new Float3(0.2916132853f, 0.3312535401f, 0.08793624968f), new Float3(0.07365265219f, -0.1666159848f, 0.411478311f), new Float3(-0.26126526f, -0.2422237692f, 0.2748965434f), new Float3(-0.3721862032f, 0.252790166f, 0.008634938242f), new Float3(-0.3691191571f, -0.255281188f, 0.03290232422f), new Float3(0.2278441737f, -0.3358364886f, 0.1944244981f), new Float3(0.363398169f, -0.2310190248f, 0.1306597909f),
		new Float3(-0.304231482f, -0.2698452035f, 0.1926830856f), new Float3(-0.3199312232f, 0.316332536f, -0.008816977938f), new Float3(0.2874852279f, 0.1642275508f, -0.304764754f), new Float3(-0.1451096801f, 0.3277541114f, -0.2720669462f), new Float3(0.3220090754f, 0.0511344108f, 0.3101538769f), new Float3(-0.1247400865f, -0.04333605335f, -0.4301882115f), new Float3(-0.2829555867f, -0.3056190617f, -0.1703910946f), new Float3(0.1069384374f, 0.3491024667f, -0.2630430352f),
		new Float3(-0.1420661144f, -0.3055376754f, -0.2982682484f), new Float3(-0.250548338f, 0.3156466809f, -0.2002316239f), new Float3(0.3265787872f, 0.1871229129f, 0.2466400438f), new Float3(0.07646097258f, -0.3026690852f, 0.324106687f), new Float3(0.3451771584f, 0.2757120714f, -0.0856480183f), new Float3(0.298137964f, 0.2852657134f, 0.179547284f), new Float3(0.2812250376f, 0.3466716415f, 0.05684409612f), new Float3(0.4390345476f, -0.09790429955f, -0.01278335452f),
		new Float3(0.2148373234f, 0.1850172527f, 0.3494474791f), new Float3(0.2595421179f, -0.07946825393f, 0.3589187731f), new Float3(0.3182823114f, -0.307355516f, -0.08203022006f), new Float3(-0.4089859285f, -0.04647718411f, 0.1818526372f), new Float3(-0.2826749061f, 0.07417482322f, 0.3421885344f), new Float3(0.3483864637f, 0.225442246f, -0.1740766085f), new Float3(-0.3226415069f, -0.1420585388f, -0.2796816575f), new Float3(0.4330734858f, -0.118868561f, -0.02859407492f),
		new Float3(-0.08717822568f, -0.3909896417f, -0.2050050172f), new Float3(-0.2149678299f, 0.3939973956f, -0.03247898316f), new Float3(-0.2687330705f, 0.322686276f, -0.1617284888f), new Float3(0.2105665099f, -0.1961317136f, -0.3459683451f), new Float3(0.4361845915f, -0.1105517485f, 0.004616608544f), new Float3(0.05333333359f, -0.313639498f, -0.3182543336f), new Float3(-0.05986216652f, 0.1361029153f, -0.4247264031f), new Float3(0.3664988455f, 0.2550543014f, -0.05590974511f),
		new Float3(-0.2341015558f, -0.182405731f, 0.3382670703f), new Float3(-0.04730947785f, -0.4222150243f, -0.1483114513f), new Float3(-0.2391566239f, -0.2577696514f, -0.2808182972f), new Float3(-0.1242081035f, 0.4256953395f, -0.07652336246f), new Float3(0.2614832715f, -0.3650179274f, 0.02980623099f), new Float3(-0.2728794681f, -0.3499628774f, 0.07458404908f), new Float3(0.007892900508f, -0.1672771315f, 0.4176793787f), new Float3(-0.01730330376f, 0.2978486637f, -0.3368779738f),
		new Float3(0.2054835762f, -0.3252600376f, -0.2334146693f), new Float3(-0.3231994983f, 0.1564282844f, -0.2712420987f), new Float3(-0.2669545963f, 0.2599343665f, -0.2523278991f), new Float3(-0.05554372779f, 0.3170813944f, -0.3144428146f), new Float3(-0.2083935713f, -0.310922837f, -0.2497981362f), new Float3(0.06989323478f, -0.3156141536f, 0.3130537363f), new Float3(0.3847566193f, -0.1605309138f, -0.1693876312f), new Float3(-0.3026215288f, -0.3001537679f, -0.1443188342f),
		new Float3(0.3450735512f, 0.08611519592f, 0.2756962409f), new Float3(0.1814473292f, -0.2788782453f, -0.3029914042f), new Float3(-0.03855010448f, 0.09795110726f, 0.4375151083f), new Float3(0.3533670318f, 0.2665752752f, 0.08105160988f), new Float3(-0.007945601311f, 0.140359426f, -0.4274764309f), new Float3(0.4063099273f, -0.1491768253f, -0.1231199324f), new Float3(-0.2016773589f, 0.008816271194f, -0.4021797064f), new Float3(-0.07527055435f, -0.425643481f, -0.1251477955f),
	};
	
	private static int fastRound(float f) {
		return (f >= 0) ? (int) (f + 0.5f) : (int) (f - 0.5f);
	}
	
	private static int fastFloor(float f) {
		return (f >= 0 ? (int) f : (int) f - 1);
	}
	
	private static float lerp(float a, float b, float t) {
		return a + t * (b - a);
	}
	
	private static float InterpQuinticFunc(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	private final static int X_PRIME = 1619;
	private final static int Y_PRIME = 31337;
	private final static int Z_PRIME = 6971;
	
	private static int valueHash(int seed, int x, int y, int z) {
		int hash = seed;
		hash ^= X_PRIME * x;
		hash ^= Y_PRIME * y;
		hash ^= Z_PRIME * z;
		
		hash = hash * hash * hash * 60493;
		hash = (hash >> 13) ^ hash;
		
		return hash;
	}
	
	private static float GradCoord3D(int seed, int x, int y, int z, float xd, float yd, float zd) {
		int hash = seed;
		hash ^= X_PRIME * x;
		hash ^= Y_PRIME * y;
		hash ^= Z_PRIME * z;
		
		hash = hash * hash * hash * 60493;
		hash = (hash >> 13) ^ hash;
		
		Float3 g = GRAD_3D[hash & 15];
		
		return xd * g.x + yd * g.y + zd * g.z;
	}
	
	private static float valueCoordinate(int seed, int x, int y, int z) {
		int n = seed;
		n ^= X_PRIME * x;
		n ^= Y_PRIME * y;
		n ^= Z_PRIME * z;
		
		return (n * n * n * 60493) / 2147483648f;
	}
	
	private static float getFractalBounding(int octaves, double ampGain) {
		float amp = (float) ampGain;
		float ampFractal = 1;
		for (int i = 1; i < octaves; i++) {
			ampFractal += amp;
			amp *= ampGain;
		}
		return 1 / ampFractal;
	}
	
	public static double getCellular(Location location, double frequency, float scatter, int seed, CellularReturnType returnType, CellEdgeType edgeType) {
		float x = (float) (location.getX() * frequency);
		float y = (float) (location.getY() * frequency);
		float z = (float) (location.getZ() * frequency);
		
		return switch (returnType) {
			case VORONOI, PRIMARY_DISTANCE -> singleCellular(x, y, z, scatter, seed, returnType, edgeType);
			default -> singleCellular2Edge(x, y, z, scatter, seed, returnType, edgeType);
		};
	}
	
	private static float singleCellular(float x, float y, float z, float scatter, int seed, CellularReturnType returnType, CellEdgeType edgeType) {
		int xr = fastRound(x);
		int yr = fastRound(y);
		int zr = fastRound(z);
		
		float distance = 999999;
		int xc = 0, yc = 0, zc = 0;
		
		switch (edgeType) {
			case EUCLIDEAN:
				for (int xi = xr - 1; xi <= xr + 1; xi++) {
					for (int yi = yr - 1; yi <= yr + 1; yi++) {
						for (int zi = zr - 1; zi <= zr + 1; zi++) {
							Float3 vec = CELL_3D[valueHash(seed, xi, yi, zi) & 255];
							
							float vecX = xi - x + vec.x * scatter;
							float vecY = yi - y + vec.y * scatter;
							float vecZ = zi - z + vec.z * scatter;
							
							float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;
							
							if (newDistance < distance) {
								distance = newDistance;
								xc = xi;
								yc = yi;
								zc = zi;
							}
						}
					}
				}
				break;
			case MANHATTAN:
				for (int xi = xr - 1; xi <= xr + 1; xi++) {
					for (int yi = yr - 1; yi <= yr + 1; yi++) {
						for (int zi = zr - 1; zi <= zr + 1; zi++) {
							Float3 vec = CELL_3D[valueHash(seed, xi, yi, zi) & 255];
							
							float vecX = xi - x + vec.x * scatter;
							float vecY = yi - y + vec.y * scatter;
							float vecZ = zi - z + vec.z * scatter;
							
							float newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ);
							
							if (newDistance < distance) {
								distance = newDistance;
								xc = xi;
								yc = yi;
								zc = zi;
							}
						}
					}
				}
				break;
			case NATURAL:
				for (int xi = xr - 1; xi <= xr + 1; xi++) {
					for (int yi = yr - 1; yi <= yr + 1; yi++) {
						for (int zi = zr - 1; zi <= zr + 1; zi++) {
							Float3 vec = CELL_3D[valueHash(seed, xi, yi, zi) & 255];
							
							float vecX = xi - x + vec.x * scatter;
							float vecY = yi - y + vec.y * scatter;
							float vecZ = zi - z + vec.z * scatter;
							
							float newDistance = (Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);
							
							if (newDistance < distance) {
								distance = newDistance;
								xc = xi;
								yc = yi;
								zc = zi;
							}
						}
					}
				}
				break;
		}
		
		float value = switch (returnType) {
			case VORONOI -> valueCoordinate(seed, xc, yc, zc);
			case PRIMARY_DISTANCE -> distance - 1;
			default -> 0;
		};
		
		if (value > 0.99) return 0.99f;
		
		// Absolute 0 to 1
		value = (value / 2f) + 0.5f;
		
		return value;
		
	}
	
	private static double singleCellular2Edge(float x, float y, float z, float scatter, int seed, CellularReturnType returnType, CellEdgeType edgeType) {
		int xr = fastRound(x);
		int yr = fastRound(y);
		int zr = fastRound(z);
		
		float distance = 999999;
		float distance2 = 999999;
		
		switch (edgeType) {
			case EUCLIDEAN:
				for (int xi = xr - 1; xi <= xr + 1; xi++) {
					for (int yi = yr - 1; yi <= yr + 1; yi++) {
						for (int zi = zr - 1; zi <= zr + 1; zi++) {
							Float3 vec = CELL_3D[valueHash(seed, xi, yi, zi) & 255];
							
							float vecX = xi - x + vec.x * scatter;
							float vecY = yi - y + vec.y * scatter;
							float vecZ = zi - z + vec.z * scatter;
							
							float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;
							
							distance2 = Math.max(Math.min(distance2, newDistance), distance);
							distance = Math.min(distance, newDistance);
						}
					}
				}
				break;
			case MANHATTAN:
				for (int xi = xr - 1; xi <= xr + 1; xi++) {
					for (int yi = yr - 1; yi <= yr + 1; yi++) {
						for (int zi = zr - 1; zi <= zr + 1; zi++) {
							Float3 vec = CELL_3D[valueHash(seed, xi, yi, zi) & 255];
							
							float vecX = xi - x + vec.x * scatter;
							float vecY = yi - y + vec.y * scatter;
							float vecZ = zi - z + vec.z * scatter;
							
							float newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ);
							
							distance2 = Math.max(Math.min(distance2, newDistance), distance);
							distance = Math.min(distance, newDistance);
						}
					}
				}
				break;
			case NATURAL:
				for (int xi = xr - 1; xi <= xr + 1; xi++) {
					for (int yi = yr - 1; yi <= yr + 1; yi++) {
						for (int zi = zr - 1; zi <= zr + 1; zi++) {
							Float3 vec = CELL_3D[valueHash(seed, xi, yi, zi) & 255];
							
							float vecX = xi - x + vec.x * scatter;
							float vecY = yi - y + vec.y * scatter;
							float vecZ = zi - z + vec.z * scatter;
							
							float newDistance = (Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ)) + (vecX * vecX + vecY * vecY + vecZ * vecZ);
							
							distance2 = Math.max(Math.min(distance2, newDistance), distance);
							distance = Math.min(distance, newDistance);
						}
					}
				}
				break;
			default:
				break;
		}
		
		float value = switch (returnType) {
			case SECONDARY_DISTANCE -> distance2 - 1;
			case ADDITIVE_DISTANCES -> distance2 + distance - 1;
			case SUBTRACTIVE_DISTANCES -> distance2 - distance - 1;
			case MULTIPLICATIVE_DISTANCES -> distance2 * distance - 1;
			case DIVISIVE_DISTANCES -> distance / distance2 - 1;
			default -> 0;
		};
		
		if (value > 0.99) return 0.99;
		
		// Absolute 0 to 1
		value = (value / 2f) + 0.5f;
		
		return value;
		
	}
	
	public static double getPerlinFractal(Location location, double frequency, int octaves, double freqGain, double ampGain, int seed, FractalType fractalType) {
		float x = (float) (location.getX() * frequency + 0.1);
		float y = (float) (location.getY() * frequency + 0.1);
		float z = (float) (location.getZ() * frequency + 0.1);
		
		float value = switch (fractalType) {
			case BROWNIAN -> SinglePerlinFractalBrownian(x, y, z, octaves, freqGain, ampGain, seed);
			case BILLOW -> SinglePerlinFractalBillow(x, y, z, octaves, freqGain, ampGain, seed);
			case RIGID -> SinglePerlinFractalRigid(x, y, z, octaves, freqGain, ampGain, seed);
		};
		
		// Absolute 0 to 1
		value = (value / 2f) + 0.5f;
		return value;
	}
	
	private static float SinglePerlinFractalBrownian(float x, float y, float z, int octaves, double freqGain, double ampGain, int seed) {
		float sum = SinglePerlin(seed, x, y, z);
		float amp = 1;
		
		for (int i = 1; i < octaves; i++) {
			x *= freqGain;
			y *= freqGain;
			z *= freqGain;
			
			amp *= ampGain;
			sum += SinglePerlin(++seed, x, y, z) * amp;
		}
		
		return sum * getFractalBounding(octaves, ampGain);
	}
	
	private static float SinglePerlinFractalBillow(float x, float y, float z, int octaves, double freqGain, double ampGain, int seed) {
		float sum = Math.abs(SinglePerlin(seed, x, y, z)) * 2 - 1;
		float amp = 1;
		
		for (int i = 1; i < octaves; i++) {
			x *= freqGain;
			y *= freqGain;
			z *= freqGain;
			
			amp *= ampGain;
			sum += (Math.abs(SinglePerlin(++seed, x, y, z)) * 2 - 1) * amp;
		}
		
		return sum * getFractalBounding(octaves, ampGain);
	}
	
	private static float SinglePerlinFractalRigid(float x, float y, float z, int octaves, double freqGain, double ampGain, int seed) {
		float sum = 1 - Math.abs(SinglePerlin(seed, x, y, z));
		float amp = 1;
		
		for (int i = 1; i < octaves; i++) {
			x *= freqGain;
			y *= freqGain;
			z *= freqGain;
			
			amp *= ampGain;
			sum -= (1 - Math.abs(SinglePerlin(++seed, x, y, z))) * amp;
		}
		
		return sum;
	}
	
	private static float SinglePerlin(int seed, float x, float y, float z) {
		int x0 = fastFloor(x);
		int y0 = fastFloor(y);
		int z0 = fastFloor(z);
		
		int x1 = x0 + 1;
		int y1 = y0 + 1;
		int z1 = z0 + 1;
		
		float xs = InterpQuinticFunc(x - x0);
		float ys = InterpQuinticFunc(y - y0);
		float zs = InterpQuinticFunc(z - z0);
		
		float xd0 = x - x0;
		float yd0 = y - y0;
		float zd0 = z - z0;
		
		float xd1 = xd0 - 1;
		float yd1 = yd0 - 1;
		float zd1 = zd0 - 1;
		
		float xf00 = lerp(GradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), GradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs);
		float xf10 = lerp(GradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), GradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs);
		float xf01 = lerp(GradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), GradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs);
		float xf11 = lerp(GradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), GradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs);
		
		float yf0 = lerp(xf00, xf10, ys);
		float yf1 = lerp(xf01, xf11, ys);
		
		return lerp(yf0, yf1, zs);
	}
	
	
}