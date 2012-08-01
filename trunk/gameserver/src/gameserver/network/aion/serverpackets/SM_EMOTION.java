/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.network.aion.serverpackets;


import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.calc.Stat2;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * Emotion packet
 * 
 * @author SoulKeeper
 */
public class SM_EMOTION extends AionServerPacket {

	/**
	 * Object id of emotion sender
	 */
	private int senderObjectId;

	/**
	 * Some unknown variable
	 */
	private EmotionType emotionType;

	/**
	 * ID of emotion
	 */
	private int emotion;

	/**
	 * Object id of emotion target
	 */
	private int targetObjectId;

	/**
	 * Temporary Speed..
	 */
	private float speed;
	private int state;
	private int baseAttackSpeed;
	private int currentAttackSpeed;

	/**
	 * Coordinates of player
	 */
	private float x;
	private float y;
	private float z;
	private byte heading;

	/**
	 * This constructor should be used when emotion and targetid is 0
	 * 
	 * @param creature
	 * @param emotionType
	 */
	public SM_EMOTION(Creature creature, EmotionType emotionType) {
		this(creature, emotionType, 0, 0);
	}

	/**
	 * Constructs new server packet with specified opcode
	 * 
	 * @param senderObjectId
	 *          who sended emotion
	 * @param unknown
	 *          Dunno what it is, can be 0x10 or 0x11
	 * @param emotionId
	 *          emotion to play
	 * @param emotionId
	 *          who target emotion
	 */
	public SM_EMOTION(Creature creature, EmotionType emotionType, int emotion, int targetObjectId) {
		this.senderObjectId = creature.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.targetObjectId = targetObjectId;
		this.state = creature.getState();
		Stat2 aSpeed = creature.getGameStats().getAttackSpeed();
		this.baseAttackSpeed = aSpeed.getBase();
		this.currentAttackSpeed = aSpeed.getCurrent();
		this.speed = creature.getGameStats().getMovementSpeedFloat();
	}

	/**
	 * @param Obj
	 * @param doorId
	 */
	public SM_EMOTION(int Objid, EmotionType emotionType) {
		this.senderObjectId = Objid;
		this.emotionType = emotionType;
	}
        
       public SM_EMOTION(int doorId) {
        this.senderObjectId = doorId;
        this.emotionType = EmotionType.OPEN_DOOR;
       }

	/**
	 * New
	 */
	public SM_EMOTION(Player player, EmotionType emotionType, int emotion, float x, float y, float z, byte heading, int targetObjectId) {
		this.senderObjectId = player.getObjectId();
		this.emotionType = emotionType;
		this.emotion = emotion;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.targetObjectId = targetObjectId;

		this.state = player.getState();
		this.speed = player.getGameStats().getMovementSpeedFloat();
		Stat2 aSpeed = player.getGameStats().getAttackSpeed();
		this.baseAttackSpeed = aSpeed.getBase();
		this.currentAttackSpeed = aSpeed.getCurrent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(senderObjectId);
		writeC(emotionType.getTypeId());
		switch (emotionType) {
			case SELECT_TARGET:
				// select target
				writeH(state);
				writeF(speed);
				break;
			case JUMP:
				writeH(state);
				writeF(speed);
				break;
			case SIT:
				// sit
				writeH(state);
				writeF(speed);
				break;
			case STAND:
				// stand
				writeH(state);
				writeF(speed);
				break;
			case CHAIR_SIT:
				// sit (chair)
				writeH(state);
				writeF(speed);
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case CHAIR_UP:
				// stand (chair)
				writeH(state);
				writeF(speed);
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case START_FLYTELEPORT:
				// fly teleport (start)
				writeH(state);
				writeF(speed);
				writeD(emotion); // teleport Id
				break;
			case WINDSTREAM:
				// entering windstream
				speed = 7.32f;//From packets send seems to be a fixed value rather then variable, maybe changed later.
				writeH(2);
				writeF(speed);
				writeD(emotion); // teleport Id
				writeD(targetObjectId); // distance
				break;
			case WINDSTREAM_BOOST:
				speed = 7.32f;//Odd that the value doesn't change with boost...
				writeH(2);
				writeF(speed);
				break;
			case WINDSTREAM_END:
				speed = 7.32f;
				writeC(1);
				writeC(2);				
				writeF(speed);
				break;
			case LAND_FLYTELEPORT:
				// fly teleport (land)
				writeH(state);
				writeF(speed);
				break;
			case FLY:
				// toggle flight mode
				writeH(state);
				writeF(speed);
				break;
			case LAND:
				// toggle land mode
				writeH(state);
				writeF(speed);
				break;
			case DIE:
				// die
				writeH(state);
				writeF(speed);
				writeD(targetObjectId);
				break;
			case RESURRECT:
				// resurrect
				writeH(state);
				writeF(speed);
				break;
			case EMOTE:
				// emote
				writeH(state);
				writeF(speed);
				writeD(targetObjectId);
				writeH(emotion);
				writeC(1);
				break;
			case ATTACKMODE:
				// toggle attack mode
				writeH(state);
				writeF(speed);
				break;
			case NEUTRALMODE:
				// toggle normal mode
				writeH(state);
				writeF(speed);
				break;
			case WALK:
				// toggle walk
				writeH(state);
				writeF(speed);
				break;
			case RUN:
				// toggle run
				writeH(state);
				writeF(speed);
				break;
			case OPEN_DOOR:
				writeD(0x09);
				writeH(0);
				break;
			case CLOSE_DOOR:
				writeD(0x0A);
				writeD(0x00);
				writeC(0);
				break;
			case OPEN_PRIVATESHOP:
				// private shop open
				writeH(state);
				writeF(0);
				break;
			case CLOSE_PRIVATESHOP:
				// private shop close
				writeH(state);
				writeF(speed);
				break;
			case START_EMOTE2:
				// emote startloop
				writeH(state);
				writeF(speed);
				writeH(baseAttackSpeed);
				writeH(currentAttackSpeed);
				break;
			case POWERSHARD_ON:
				// powershard on
				writeH(state);
				writeF(speed);
				break;
			case POWERSHARD_OFF:
				// powershard off
				writeH(state);
				writeF(speed);
				break;
			case ATTACKMODE2:
				// toggle attack mode
				writeH(state);
				writeF(speed);
				break;
			case NEUTRALMODE2:
				// toggle normal mode
				writeH(state);
				writeF(speed);
				break;
			case START_LOOT:
				// looting start
				writeH(state);
				writeF(speed);
				writeD(targetObjectId);
				break;
			case END_LOOT:
				// looting end
				writeH(state);
				writeF(speed);
				writeD(targetObjectId);
				break;
			case START_QUESTLOOT:
				// looting start (quest)
				writeH(state);
				writeF(speed);
				writeD(targetObjectId);
				break;
			case END_QUESTLOOT:
				// looting end (quest)
				writeH(state);
				writeF(speed);
				writeD(targetObjectId);
				break;
			case START_FEEDING:
			case END_FEEDING:
				writeH(1);
				writeF(speed);
				break;
			default:
				writeH(state);
				writeF(speed);
				if (targetObjectId != 0) {
					writeD(targetObjectId);
				}
		}
	}
}
