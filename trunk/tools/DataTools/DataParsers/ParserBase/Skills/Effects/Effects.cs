﻿namespace Jamie.Skills
{
	using System;
	using System.Collections.Generic;
	using System.ComponentModel;
	using System.Xml.Schema;
	using System.Xml.Serialization;

	[Serializable]
	public partial class Effects
	{
		public Effects() {
			this.EffectList = new List<Effect>();
		}

		[XmlElement("root", Form = XmlSchemaForm.Unqualified, Type = typeof(RootEffect))]
		[XmlElement("stun", Form = XmlSchemaForm.Unqualified, Type = typeof(StunEffect))]
		[XmlElement("sleep", Form = XmlSchemaForm.Unqualified, Type = typeof(SleepEffect))]
		[XmlElement("snare", Form = XmlSchemaForm.Unqualified, Type = typeof(SnareEffect))]
		[XmlElement("slow", Form = XmlSchemaForm.Unqualified, Type = typeof(SlowEffect))]
		[XmlElement("poison", Form = XmlSchemaForm.Unqualified, Type = typeof(PoisonEffect))]
		[XmlElement("bleed", Form = XmlSchemaForm.Unqualified, Type = typeof(BleedEffect))]
		[XmlElement("stumble", Form = XmlSchemaForm.Unqualified, Type = typeof(StumbleEffect))]
		[XmlElement("spin", Form = XmlSchemaForm.Unqualified, Type = typeof(SpinEffect))]
		[XmlElement("stagger", Form = XmlSchemaForm.Unqualified, Type = typeof(StaggerEffect))]
		[XmlElement("openaerial", Form = XmlSchemaForm.Unqualified, Type = typeof(OpenAerialEffect))]
		[XmlElement("closeaerial", Form = XmlSchemaForm.Unqualified, Type = typeof(CloseAerialEffect))]
		[XmlElement("bind", Form = XmlSchemaForm.Unqualified, Type = typeof(BindEffect))]
		[XmlElement("shield", Form = XmlSchemaForm.Unqualified, Type = typeof(ShieldEffect))]
		[XmlElement("dispel", Form = XmlSchemaForm.Unqualified, Type = typeof(DispelEffect))]
		[XmlElement("statup", Form = XmlSchemaForm.Unqualified, Type = typeof(StatupEffect))]
		[XmlElement("statboost", Form = XmlSchemaForm.Unqualified, Type = typeof(StatboostEffect))]
		[XmlElement("weaponstatboost", Form = XmlSchemaForm.Unqualified, Type = typeof(WeaponStatboostEffect))]
		[XmlElement("wpnmastery", Form = XmlSchemaForm.Unqualified, Type = typeof(WeaponMasteryEffect))]
		[XmlElement("statdown", Form = XmlSchemaForm.Unqualified, Type = typeof(StatdownEffect))]
		[XmlElement("spellatk", Form = XmlSchemaForm.Unqualified, Type = typeof(SpellAttackEffect))]
		[XmlElement("deform", Form = XmlSchemaForm.Unqualified, Type = typeof(DeformEffect))]
		[XmlElement("shapechange", Form = XmlSchemaForm.Unqualified, Type = typeof(ShapeChangeEffect))]
		[XmlElement("polymorph", Form = XmlSchemaForm.Unqualified, Type = typeof(PolymorphEffect))]
		[XmlElement("hide", Form = XmlSchemaForm.Unqualified, Type = typeof(HideEffect))]
		[XmlElement("search", Form = XmlSchemaForm.Unqualified, Type = typeof(SearchEffect))]
		[XmlElement("heal", Form = XmlSchemaForm.Unqualified, Type = typeof(HealOverTimeEffect))]
		[XmlElement("heal_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(HealInstantEffect))]
		[XmlElement("mpheal", Form = XmlSchemaForm.Unqualified, Type = typeof(MpHealEffect))]
		[XmlElement("mpheal_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(MpHealInstantEffect))]
		[XmlElement("nofly", Form = XmlSchemaForm.Unqualified, Type = typeof(NoFlyEffect))]
		[XmlElement("dpheal", Form = XmlSchemaForm.Unqualified, Type = typeof(DpHealEffect))]
		[XmlElement("dpheal_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(DpHealInstantEffect))]
		[XmlElement("fpheal", Form = XmlSchemaForm.Unqualified, Type = typeof(FpHealEffect))]
		[XmlElement("fpheal_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(FpHealInstantEffect))]
		[XmlElement("skillatk_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(SkillAttackInstantEffect))]
		[XmlElement("spellatk_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(SpellAttackInstantEffect))]
		[XmlElement("dash", Form = XmlSchemaForm.Unqualified, Type = typeof(DashEffect))]
		[XmlElement("backdash", Form = XmlSchemaForm.Unqualified, Type = typeof(BackDashEffect))]
		[XmlElement("delaydamage", Form = XmlSchemaForm.Unqualified, Type = typeof(DelayDamageEffect))]
		[XmlElement("return", Form = XmlSchemaForm.Unqualified, Type = typeof(ReturnEffect))]
		[XmlElement("itemheal", Form = XmlSchemaForm.Unqualified, Type = typeof(ItemHealEffect))]
		[XmlElement("itemhealmp", Form = XmlSchemaForm.Unqualified, Type = typeof(ItemHealMpEffect))]
		[XmlElement("itemhealdp", Form = XmlSchemaForm.Unqualified, Type = typeof(ItemHealDpEffect))]
		[XmlElement("itemhealfp", Form = XmlSchemaForm.Unqualified, Type = typeof(ItemHealFpEffect))]
		[XmlElement("carvesignet", Form = XmlSchemaForm.Unqualified, Type = typeof(CarveSignetEffect))]
		[XmlElement("signet", Form = XmlSchemaForm.Unqualified, Type = typeof(SignetEffect))]
		[XmlElement("signetburst", Form = XmlSchemaForm.Unqualified, Type = typeof(SignetBurstEffect))]
		[XmlElement("silence", Form = XmlSchemaForm.Unqualified, Type = typeof(SilenceEffect))]
		[XmlElement("curse", Form = XmlSchemaForm.Unqualified, Type = typeof(CurseEffect))]
		[XmlElement("blind", Form = XmlSchemaForm.Unqualified, Type = typeof(BlindEffect))]
		[XmlElement("disease", Form = XmlSchemaForm.Unqualified, Type = typeof(DiseaseEffect))]
		[XmlElement("boosthate", Form = XmlSchemaForm.Unqualified, Type = typeof(BoostHateEffect))]
		[XmlElement("hostileup", Form = XmlSchemaForm.Unqualified, Type = typeof(HostileUpEffect))]
		[XmlElement("paralyze", Form = XmlSchemaForm.Unqualified, Type = typeof(ParalyzeEffect))]
		[XmlElement("confuse", Form = XmlSchemaForm.Unqualified, Type = typeof(ConfuseEffect))]
		[XmlElement("alwaysresist", Form = XmlSchemaForm.Unqualified, Type = typeof(AlwaysResistEffect))]
		[XmlElement("alwaysblock", Form = XmlSchemaForm.Unqualified, Type = typeof(AlwaysBlockEffect))]
		[XmlElement("alwaysparry", Form = XmlSchemaForm.Unqualified, Type = typeof(AlwaysParryEffect))]
		[XmlElement("alwaysdodge", Form = XmlSchemaForm.Unqualified, Type = typeof(AlwaysDodgeEffect))]
		[XmlElement("dispeldebuffphysical", Form = XmlSchemaForm.Unqualified, Type = typeof(DispelDebuffPhysicalEffect))]
		[XmlElement("dispeldebuff", Form = XmlSchemaForm.Unqualified, Type = typeof(DispelDebuffEffect))]
		[XmlElement("mpuseovertime", Form = XmlSchemaForm.Unqualified, Type = typeof(MpUseOverTimeEffect))]
		[XmlElement("hpuseovertime", Form = XmlSchemaForm.Unqualified, Type = typeof(HpUseOverTimeEffect))]
		[XmlElement("switchhpmp", Form = XmlSchemaForm.Unqualified, Type = typeof(SwitchHpMpEffect))]
		[XmlElement("aura", Form = XmlSchemaForm.Unqualified, Type = typeof(AuraEffect))]
		[XmlElement("summon", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonEffect))]
		[XmlElement("fear", Form = XmlSchemaForm.Unqualified, Type = typeof(FearEffect))]
		[XmlElement("resurrect", Form = XmlSchemaForm.Unqualified, Type = typeof(ResurrectEffect))]
		[XmlElement("dispeldebuffmental", Form = XmlSchemaForm.Unqualified, Type = typeof(DispelDebuffMentalEffect))]
		[XmlElement("reflector", Form = XmlSchemaForm.Unqualified, Type = typeof(ReflectorEffect))]
		[XmlElement("returnpoint", Form = XmlSchemaForm.Unqualified, Type = typeof(ReturnPointEffect))]
		[XmlElement("provoker", Form = XmlSchemaForm.Unqualified, Type = typeof(ProvokerEffect))]
		[XmlElement("spellatkdraininstant", Form = XmlSchemaForm.Unqualified, Type = typeof(SpellAtkDrainInstantEffect))]
		[XmlElement("onetimeboostskillattack", Form = XmlSchemaForm.Unqualified, Type = typeof(OneTimeBoostSkillAttackEffect))]
		[XmlElement("armormastery", Form = XmlSchemaForm.Unqualified, Type = typeof(ArmorMasteryEffect))]
		[XmlElement("weaponstatup", Form = XmlSchemaForm.Unqualified, Type = typeof(WeaponStatupEffect))]
		[XmlElement("boostskillcastingtime", Form = XmlSchemaForm.Unqualified, Type = typeof(BoostSkillCastingTimeEffect))]
		[XmlElement("summontrap", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonTrapEffect))]
		[XmlElement("summongroupgate", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonGroupGateEffect))]
		[XmlElement("summonservant", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonServantEffect))]
		[XmlElement("skillatkdraininstant", Form = XmlSchemaForm.Unqualified, Type = typeof(SkillAtkDrainInstantEffect))]
		[XmlElement("petorderuseultraskill", Form = XmlSchemaForm.Unqualified, Type = typeof(PetOrderUseUltraSkillEffect))]
		[XmlElement("boostheal", Form = XmlSchemaForm.Unqualified, Type = typeof(BoostHealEffect))]
		[XmlElement("dispelbuff", Form = XmlSchemaForm.Unqualified, Type = typeof(DispelBuffEffect))]
		[XmlElement("skilllauncher", Form = XmlSchemaForm.Unqualified, Type = typeof(SkillLauncherEffect))]
		[XmlElement("pulled", Form = XmlSchemaForm.Unqualified, Type = typeof(PulledEffect))]
		[XmlElement("movebehind", Form = XmlSchemaForm.Unqualified, Type = typeof(MoveBehindEffect))]
		[XmlElement("rebirth", Form = XmlSchemaForm.Unqualified, Type = typeof(RebirthEffect))]
		[XmlElement("onetimeboostskillcritical", Form = XmlSchemaForm.Unqualified, Type = typeof(OneTimeBoostSkillCriticalEffect))]
		[XmlElement("changemp", Form = XmlSchemaForm.Unqualified, Type = typeof(ChangeMpConsumptionEffect))]
		[XmlElement("resurrectbase", Form = XmlSchemaForm.Unqualified, Type = typeof(ResurrectBaseEffect))]
		[XmlElement("magiccounteratk", Form = XmlSchemaForm.Unqualified, Type = typeof(MagicCounterAtkEffect))]
		[XmlElement("dispelbuffcounteratk", Form = XmlSchemaForm.Unqualified, Type = typeof(DispelBuffCounterAtkEffect))]
		[XmlElement("procatk_instant", Form = XmlSchemaForm.Unqualified, Type = typeof(ProcAtkInstantEffect))]
		[XmlElement("deboostheal", Form = XmlSchemaForm.Unqualified, Type = typeof(DeboostHealEffect))]
		[XmlElement("onetimeboostheal", Form = XmlSchemaForm.Unqualified, Type = typeof(OneTimeBoostHealEffect))]
		[XmlElement("protect", Form = XmlSchemaForm.Unqualified, Type = typeof(ProtectEffect))]

		[XmlElement("mpatk", Form = XmlSchemaForm.Unqualified, Type = typeof(MpAttackEffect))]
		[XmlElement("mpatkinstant", Form = XmlSchemaForm.Unqualified, Type = typeof(MpAttackInstantEffect))]
		[XmlElement("healcastoronatk", Form = XmlSchemaForm.Unqualified, Type = typeof(HealCastorOnAttackedEffect))]
		[XmlElement("randommoveloc", Form = XmlSchemaForm.Unqualified, Type = typeof(RandomMoveLocEffect))]
		[XmlElement("switchhostile", Form = XmlSchemaForm.Unqualified, Type = typeof(SwitchHostileEffect))]
		[XmlElement("fpatk", Form = XmlSchemaForm.Unqualified, Type = typeof(FpAttackEffect))]
		[XmlElement("delayedfpatk", Form = XmlSchemaForm.Unqualified, Type = typeof(DelayedFPAttackInstantEffect))]
		[XmlElement("evade", Form = XmlSchemaForm.Unqualified, Type = typeof(EvadeEffect))]
        [XmlElement("xpboost", Form = XmlSchemaForm.Unqualified, Type = typeof(XPBoostEffect))]
        [XmlElement("spellatkdrain", Form = XmlSchemaForm.Unqualified, Type = typeof(SpellAtkDrainEffect))]
        [XmlElement("buffsilence", Form = XmlSchemaForm.Unqualified, Type = typeof(BuffSilenceEffect))]
        [XmlElement("buffbind", Form = XmlSchemaForm.Unqualified, Type = typeof(BuffBindEffect))]
        [XmlElement("buffsleep", Form = XmlSchemaForm.Unqualified, Type = typeof(BuffSleepEffect))]
        [XmlElement("buffstun", Form = XmlSchemaForm.Unqualified, Type = typeof(BuffStunEffect))]
        [XmlElement("extendaurarange", Form = XmlSchemaForm.Unqualified, Type = typeof(ExtendAuraRangeEffect))]
		[XmlElement("recallinstant", Form = XmlSchemaForm.Unqualified, Type = typeof(RecallInstantEffect))]
		[XmlElement("invulnerablewing", Form = XmlSchemaForm.Unqualified, Type = typeof(InvulnerableWingEffect))]
		[XmlElement("summontotem", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonTotemEffect))]
		[XmlElement("summonskillarea", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonSkillAreaEffect))]
        [XmlElement("resurrectpositional", Form = XmlSchemaForm.Unqualified, Type = typeof(ResurrectPositionalEffect))]
		[XmlElement("summonhoming", Form = XmlSchemaForm.Unqualified, Type = typeof(SummonHomingEffect))]
		[XmlElement("fpatkinstant", Form = XmlSchemaForm.Unqualified, Type = typeof(FpAttackInstantEffect))]
        [XmlElement("dualmastery", Form = XmlSchemaForm.Unqualified, Type = typeof(WeaponDualEffect))]
        [XmlElement("shieldmastery", Form = XmlSchemaForm.Unqualified, Type = typeof(ShieldMasteryEffect))]
        [XmlElement("simpleroot", Form = XmlSchemaForm.Unqualified, Type = typeof(SimpleRootEffect))]

		public List<Effect> EffectList;

		[XmlAttribute]
		[DefaultValue(false)]
		public bool food;
	}
}
