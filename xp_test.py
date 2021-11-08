# I give up on testing this with Forge, just no.
# I'll just make equivalent implementations in python and see what's wrong.
# Minecraft's experience system will not change in the near future anyways,
# not like automated testing of this will be needed.

import math
import unittest

def auto_str(cls):
    def __str__(self):
        return "{}({})".format(
            type(self).__name__,
            ", ".join("{}={}".format(*entry) for entry in vars(self).items())
        )
    cls.__str__ = __str__
    return cls

@auto_str
class Jar:

    def __init__(self, capacity, storedXP):
        self.capacity = capacity
        self.storedXP = storedXP

    def isFull(self):
        return self.storedXP >= self.capacity

    def isEmpty(self):
        return self.storedXP <= 0

    def insertXP(self, storage):
        xpToLeveldown = round(storage.xpBarCap() * storage.experience)
        amountToRemove = 0
        if xpToLeveldown > 0:
            amountToRemove = xpToLeveldown
        elif storage.levels > 0:
            storage.levels -= 1
            amountToRemove = storage.xpBarCap()

        if amountToRemove > 0:
            amountRemoved = min(amountToRemove, self.capacity - self.storedXP)
            self.storedXP += amountRemoved
            storage.experience = (amountToRemove - amountRemoved) / storage.xpBarCap()

    def extractXP(self, storage, all=False):
        amountToAdd = 0
        if all:
            amountToAdd = self.storedXP
        else:
            amountToAdd = min(math.ceil(storage.xpBarCap() * (1.0 - storage.experience)), self.storedXP)
        storage.addExperience(amountToAdd)
        self.storedXP -= amountToAdd

@auto_str
class XPStorage:

    def __init__(self):
        self.resetXP()

    def resetXP(self):
        self.experience = 0.0
        self.levels = 0
        self.total = 0

    def hasExperience(self):
        return self.experience > 0 or self.levels > 0

    def xpBarCap(self):
        if self.levels >= 30:
            return 112 + (self.levels - 30) * 9
        if self.levels >= 15:
            return 37 + (self.levels - 15) * 5
        return 7 + self.levels * 2

    def addExperienceLevel(self, levels):
        self.levels += levels;
        if self.levels < 0:
            self._resetXP()

    def addExperience(self, amount):
        self.experience += amount / self.xpBarCap()
        self.total += amount

        while self.experience >= 1:
            self.experience = (self.experience - 1) * self.xpBarCap()
            self.addExperienceLevel(1)
            self.experience /= self.xpBarCap()


class TestXPTransfer(unittest.TestCase):

    def subtest_transfer_consistency(self, points, capacity, instant=False):
        with self.subTest(points=points, capacity=capacity, instant=instant):
            storage = XPStorage()
            storage.addExperience(points)
            initialLevels = storage.levels
            initialExperience = storage.experience
            jar = Jar(capacity, 0)

            while storage.hasExperience() and not jar.isFull():
                jar.insertXP(storage)
                self.assertTrue(initialLevels != storage.levels or not math.isclose(initialExperience, storage.experience), "Experience did not change during transfer.")
            while not jar.isEmpty():
                jar.extractXP(storage, instant)

            self.assertEqual(initialLevels, storage.levels, "Experience levels after transfer do not match.")
            self.assertAlmostEqual(initialExperience, storage.experience, msg="Experience bar ratio after transfer does not match.")

    def test_transfer_consistency(self):
        test_cases = [
            (0, 0),
            (0, 1),
            (1, 0),
            (7, 1),
            (0, 1024),
            (30, 1024),
            (1200, 1024),
            (500, 1024),
            (5000, 1024),
            (1201, 1024),
            (1203, 1024),
            (56, 2048),
            (555000, 2048),
            (555000, 4096),
            (555000, 8192),
            (555000, 2147483647)
        ]
        for case in test_cases:
            self.subtest_transfer_consistency(*case)
            self.subtest_transfer_consistency(*case, instant=True)

if __name__ == "__main__":
    unittest.main()
