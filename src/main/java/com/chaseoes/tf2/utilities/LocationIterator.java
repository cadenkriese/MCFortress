package com.chaseoes.tf2.utilities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class LocationIterator
implements Iterator<Location>
{
	    @SuppressWarnings("unused")
		private static final int gridSize = 16777216;
	    private boolean end = false;
	    private Location[] locationQueue = new Location[3];
	    private int currentLocation = 0;
	    private int currentDistance = 0;
	    private int maxDistanceInt;
	    private int secondError;
	    private int thirdError;
	    private int secondStep;
	    private int thirdStep;
	    private BlockFace mainFace;
	    private BlockFace secondFace;
	    private BlockFace thirdFace;

	    public LocationIterator(World world, Vector start, Vector direction, double yOffset, int maxDistance) {
	        Vector startClone = start.clone();
	        startClone.setY(startClone.getY() + yOffset);
	        this.currentDistance = 0;
	        Location startLocation = new Location(world, (double)((int)Math.floor(startClone.getX())), (double)((int)Math.floor(startClone.getY())), (double)((int)Math.floor(startClone.getZ())));
	        this.mainFace = this.getXFace(direction);
	        double mainDirection = this.getXLength(direction);
	        double mainPosition = this.getXPosition(direction, startClone, startLocation);
	        this.secondFace = this.getYFace(direction);
	        double secondDirection = this.getYLength(direction);
	        double secondPosition = this.getYPosition(direction, startClone, startLocation);
	        this.thirdFace = this.getZFace(direction);
	        double thirdDirection = this.getZLength(direction);
	        double thirdPosition = this.getZPosition(direction, startClone, startLocation);
	        if (this.getYLength(direction) > mainDirection) {
	            this.mainFace = this.getYFace(direction);
	            mainDirection = this.getYLength(direction);
	            mainPosition = this.getYPosition(direction, startClone, startLocation);
	            this.secondFace = this.getZFace(direction);
	            secondDirection = this.getZLength(direction);
	            secondPosition = this.getZPosition(direction, startClone, startLocation);
	            this.thirdFace = this.getXFace(direction);
	            thirdDirection = this.getXLength(direction);
	            thirdPosition = this.getXPosition(direction, startClone, startLocation);
	        }
	        if (this.getZLength(direction) > mainDirection) {
	            this.mainFace = this.getZFace(direction);
	            mainDirection = this.getZLength(direction);
	            mainPosition = this.getZPosition(direction, startClone, startLocation);
	            this.secondFace = this.getXFace(direction);
	            secondDirection = this.getXLength(direction);
	            secondPosition = this.getXPosition(direction, startClone, startLocation);
	            this.thirdFace = this.getYFace(direction);
	            thirdDirection = this.getYLength(direction);
	            thirdPosition = this.getYPosition(direction, startClone, startLocation);
	        }
	        double d = mainPosition / mainDirection;
	        double secondd = secondPosition - secondDirection * d;
	        double thirdd = thirdPosition - thirdDirection * d;
	        this.secondError = (int)Math.floor(secondd * 1.6777216E7);
	        this.secondStep = (int)Math.round(secondDirection / mainDirection * 1.6777216E7);
	        this.thirdError = (int)Math.floor(thirdd * 1.6777216E7);
	        this.thirdStep = (int)Math.round(thirdDirection / mainDirection * 1.6777216E7);
	        if (this.secondError + this.secondStep <= 0) {
	            this.secondError = - this.secondStep + 1;
	        }
	        if (this.thirdError + this.thirdStep <= 0) {
	            this.thirdError = - this.thirdStep + 1;
	        }
	        Location lastLocation = this.getRelativeLocation(startLocation, this.reverseFace(this.mainFace));
	        if (this.secondError < 0) {
	            this.secondError += 16777216;
	            lastLocation = this.getRelativeLocation(lastLocation, this.reverseFace(this.secondFace));
	        }
	        if (this.thirdError < 0) {
	            this.thirdError += 16777216;
	            lastLocation = this.getRelativeLocation(lastLocation, this.reverseFace(this.thirdFace));
	        }
	        this.secondError -= 16777216;
	        this.thirdError -= 16777216;
	        this.locationQueue[0] = lastLocation;
	        this.currentLocation = -1;
	        this.scan();
	        boolean startLocationFound = false;
	        for (int cnt = this.currentLocation; cnt >= 0; --cnt) {
	            if (!this.locationEquals(this.locationQueue[cnt], startLocation)) continue;
	            this.currentLocation = cnt;
	            startLocationFound = true;
	            break;
	        }
	        if (!startLocationFound) {
	            throw new IllegalStateException("Start location missed in LocationIterator");
	        }
	        this.maxDistanceInt = (int)Math.round((double)maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
	    }

	    private boolean locationEquals(Location a, Location b) {
	        return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
	    }

	    @SuppressWarnings("incomplete-switch")
		private BlockFace reverseFace(BlockFace face) {
	        switch (face) {
	            case UP: {
	                return BlockFace.DOWN;
	            }
	            case DOWN: {
	                return BlockFace.UP;
	            }
	            case NORTH: {
	                return BlockFace.SOUTH;
	            }
	            case SOUTH: {
	                return BlockFace.NORTH;
	            }
	            case EAST: {
	                return BlockFace.WEST;
	            }
	            case WEST: {
	                return BlockFace.EAST;
	            }
	        }
	        return null;
	    }

	    private BlockFace getXFace(Vector direction) {
	        return direction.getX() > 0.0 ? BlockFace.SOUTH : BlockFace.NORTH;
	    }

	    private BlockFace getYFace(Vector direction) {
	        return direction.getY() > 0.0 ? BlockFace.UP : BlockFace.DOWN;
	    }

	    private BlockFace getZFace(Vector direction) {
	        return direction.getZ() > 0.0 ? BlockFace.WEST : BlockFace.EAST;
	    }

	    private double getXLength(Vector direction) {
	        return Math.abs(direction.getX());
	    }

	    private double getYLength(Vector direction) {
	        return Math.abs(direction.getY());
	    }

	    private double getZLength(Vector direction) {
	        return Math.abs(direction.getZ());
	    }

	    private double getPosition(double direction, double position, int locationPosition) {
	        return direction > 0.0 ? position - (double)locationPosition : (double)(locationPosition + 1) - position;
	    }

	    private double getXPosition(Vector direction, Vector position, Location location) {
	        return this.getPosition(direction.getX(), position.getX(), location.getBlockX());
	    }

	    private double getYPosition(Vector direction, Vector position, Location location) {
	        return this.getPosition(direction.getY(), position.getY(), location.getBlockY());
	    }

	    private double getZPosition(Vector direction, Vector position, Location location) {
	        return this.getPosition(direction.getZ(), position.getZ(), location.getBlockZ());
	    }

	    @SuppressWarnings("incomplete-switch")
		public Location getRelativeLocation(Location location, BlockFace face) {
	        switch (face) {
	            case UP: {
	                return location.clone().add(0.0, 1.0, 0.0);
	            }
	            case DOWN: {
	                return location.clone().add(0.0, -1.0, 0.0);
	            }
	            case NORTH: {
	                return location.clone().add(-1.0, 0.0, 0.0);
	            }
	            case SOUTH: {
	                return location.clone().add(1.0, 0.0, 0.0);
	            }
	            case EAST: {
	                return location.clone().add(0.0, 0.0, -1.0);
	            }
	            case WEST: {
	                return location.clone().add(0.0, 0.0, 1.0);
	            }
	        }
	        return null;
	    }

	    @Override
	    public boolean hasNext() {
	        this.scan();
	        return this.currentLocation != -1;
	    }

	    @Override
	    public Location next() {
	        this.scan();
	        if (this.currentLocation <= -1) {
	            throw new NoSuchElementException();
	        }
	        return this.locationQueue[this.currentLocation--];
	    }

	    @Override
	    public void remove() {
	        throw new UnsupportedOperationException("[LocationIterator] doesn't support location removal");
	    }

	    private void scan() {
	        if (this.currentLocation >= 0) {
	            return;
	        }
	        if (this.currentDistance > this.maxDistanceInt) {
	            this.end = true;
	            return;
	        }
	        if (this.end) {
	            return;
	        }
	        ++this.currentDistance;
	        this.secondError += this.secondStep;
	        this.thirdError += this.thirdStep;
	        if (this.secondError > 0 && this.thirdError > 0) {
	            this.locationQueue[2] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
	            if ((long)this.secondStep * (long)this.thirdError < (long)this.thirdStep * (long)this.secondError) {
	                this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[2], this.secondFace);
	                this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.thirdFace);
	            } else {
	                this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[2], this.thirdFace);
	                this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.secondFace);
	            }
	            this.thirdError -= 16777216;
	            this.secondError -= 16777216;
	            this.currentLocation = 2;
	        } else if (this.secondError > 0) {
	            this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
	            this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.secondFace);
	            this.secondError -= 16777216;
	            this.currentLocation = 1;
	        } else if (this.thirdError > 0) {
	            this.locationQueue[1] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
	            this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[1], this.thirdFace);
	            this.thirdError -= 16777216;
	            this.currentLocation = 1;
	        } else {
	            this.locationQueue[0] = this.getRelativeLocation(this.locationQueue[0], this.mainFace);
	            this.currentLocation = 0;
	        }
	    }
}
