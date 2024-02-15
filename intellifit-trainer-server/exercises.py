from pose_module import *

# hangi uzuvlarin acisi kullanilmak isteniyorsa True yapilmali
is_legs = False  # for legs
is_arms = False  # for arms
is_ankles = False  # for ankles
# hangi kol test edilmek isteniyorsa o kullanilir
is_leftLimb = False
is_rightLimb = False
# tekrar sayisini sayacak olan sayac, sag ve sol uzuv icin
counter_left = 0
counter_right = 0
# direction, hareketin yonunu temsil eder, 1 icin yukari, 0 icin asagi, sag ve sol uzuv icin
dir_left = 0
dir_right = 0
# egzersiz sirasindaki uzvun aci araligi
min_degree = 40  # varsayilan deger
max_degree = 140  # varsayilan deger

# fonksiyonlar
def reset_degrees():
    global min_degree, max_degree
    min_degree = 40
    max_degree = 140

def degree_extender(ext_min=0, ext_max=0):
    global min_degree, max_degree
    min_degree += ext_min
    max_degree += ext_max

def select_limb(which_limb):
    global is_rightLimb, is_leftLimb
    if which_limb.lower() == "right":
        is_rightLimb = True
        is_leftLimb = False
    elif which_limb.lower() == "left":
        is_leftLimb = True
        is_rightLimb = False
    elif which_limb.lower() == "both":
        is_rightLimb = True
        is_leftLimb = True

def select_part(which_part):
    global is_arms, is_legs, is_ankles
    if which_part.lower() == "arms":
        is_arms = True
        is_legs = False
        is_ankles = False
    elif which_part.lower() == "legs":
        is_arms = False
        is_legs = True
        is_ankles = False
    elif which_part.lower() == "ankles":
        is_arms = False
        is_legs = False
        is_ankles = True

# exercises, tekil egzersiz tanimlamalari
# Push Up
def exc_pushUp(which_limb="left", which_part="arms"):
    degree_extender(ext_min=20)  # min_degree = 60
    select_limb(which_limb)
    select_part(which_part)

# Dumbell Curl
def exc_dumbellCurl(which_limb="left", which_part="arms"):
    degree_extender(ext_min=10)  # min_degree = 50
    select_limb(which_limb)
    select_part(which_part)

# High Knees
def exc_highKnees(which_limb="left", which_part="legs"):
    degree_extender(ext_min=30)  # min_degree = 70
    select_limb(which_limb)
    select_part(which_part)

# Cable Triceps
def exc_cableTriceps(which_limb="left", which_part="arms"):
    degree_extender(ext_min=30)  # min_degree = 70
    select_limb(which_limb)
    select_part(which_part)

def exc_mountainClimbers(which_limb="left", which_part="legs"):
    degree_extender(ext_min=20)  # min_degree = 60
    select_limb(which_limb)
    select_part(which_part)

def exc_lunge(which_limb="left", which_part="legs"):
    degree_extender(ext_min=40)  # min_degree = 80
    select_limb(which_limb)
    select_part(which_part)

def exc_pullUp(which_limb="left", which_part="arms"):
    degree_extender(ext_min=10)  # min_degree = 50
    select_limb(which_limb)
    select_part(which_part)

def exc_squat(which_limb="left", which_part="legs"):
    degree_extender(ext_min=40)  # min_degree = 80
    select_limb(which_limb)
    select_part(which_part)

def exc_jumpingRope(which_limb="left", which_part="ankles"):
    degree_extender(ext_min=0, ext_max=-90)  # min_degree = 40, max_degree = 50
    select_limb(which_limb)
    select_part(which_part)

def exc_jumpingJack(which_limb="left", which_part="ankles"):
    degree_extender(ext_min=-20, ext_max=10)  # min_degree = 20, max_degree = 150
    select_limb(which_limb)
    select_part(which_part)

# yapilacak olan egzersiz
# reset_degrees()
# exc_squat()

