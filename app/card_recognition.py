import cv2
import pytesseract

def extract_code_from_image(image_path):
    image = cv2.imread(image_path)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    code = pytesseract.image_to_string(gray, config='--psm 6')
    cleaned_code = validate_and_clean_code(code)
    return cleaned_code

def validate_and_clean_code(code):
    import re
    match = re.search(r'[A-Z0-9]{10}', code)
    return match.group(0) if match else None
