/*
 * This file is part of [Lightning]Assembly
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
package gameserver.services.tvt;



public class TvtStartRunnable implements Runnable {

    private final int tvtId;
    private int startTime;
    private int duration;
    private int level;
    private int mapId;

    public TvtStartRunnable(int tvtId, int startTime, int duration, int level, int mapId) {
        this.tvtId = tvtId;
        this.startTime = startTime;
        this.duration = duration;
        this.level = level;
        this.mapId = mapId;
    }

    @Override
    public void run() {
        TvtService.getInstance().startTvt(getTvtId(), getStartTime(), getDuration(), getLevel(), getMapId());
    }

    public int getTvtId() {
        return tvtId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }
    
    public int getLevel(){
        return level;
    }
    
    public int getMapId(){
        return mapId;
    }
}
