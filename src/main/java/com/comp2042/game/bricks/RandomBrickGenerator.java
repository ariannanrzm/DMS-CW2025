package com.comp2042.game.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private static final int BUFFER_SIZE = 5;

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        ensureBuffer();
    }

    private void ensureBuffer() {
        while (nextBricks.size() < BUFFER_SIZE) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
    }

    @Override
    public Brick getBrick() {
        ensureBuffer();
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        ensureBuffer();
        return nextBricks.peek();
    }

    @Override
    public List<Brick> getNextBricks(int count) {
        ensureBuffer();
        return nextBricks.stream().limit(count).collect(Collectors.toList());
    }

}
